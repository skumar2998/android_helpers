Android library projects with my everyday android stuff.

## Networking ##

Wrapper for java.net for easier sync and async requests:

	HttpRequest httpRequest = new HttpRequest(url, params, HttpMethods.GET);
	String response = httpRequest.start();

	HttpRequestAsync httpRequest = new HttpRequestAsync(ur;, params, HttpMethods.GET);
    httpRequest.getHandler().setCallback(new HttpCallback() {
        @Override
        public void success(Object response) {}
        @Override
        public void failed(Exception e) {}
    });    
    new Thread(httpRequest.create()).start();

httpRequest.getHandler() returns `HttpHandler` object for the request which allow to subscribe to several events:

	HttpCallback callback
    UploadCallback uploadCallback;
    DownloadCallback downloadCallback;
    CachingCallback cachingCallback;
    PostProcessCallback postProcessCallback;

`PostProcessCallback` allows you to process(decoding json, for example) response string in the request thread:

	new PostProcessCallback() {
        @Override
        public Object postProcessResponse(HttpHandler handler,  String response) {
            return new JSONObject(response);
        }
    }

`HttpApiClient` contains facade for the networking api. It possess own pool for networking and allows easier requests with relational urls:

	public void getPath(String path, Map<String, String> params, HttpCallback callback)
	public void postPath(String path, Map<String, String> params, HttpCallback callback)

## Lazy image loading ##

`RemoteBitmap` provides simple async remote `Bitmap` loading with memory and disk(if possible) caching:

	public void getCover(String url, OnBitmapGet onBitmapGet)
    public void getCover(String url, int width, int height, final OnBitmapGet onBitmapGet)
    public interface OnBitmapGet {
        void bitmapRecieved(Bitmap bitmap, Integer urlHash);
    }

## Adapters ##

`ReusableArrayAdapter` provides `ArrayAdapter` with reusability(via `ViewHolder`)

	public ReusableArrayAdapter(Context context, int layoutResourceId,
                                Class<? extends ViewHolder<T>> holderClass, List<T> items)

`ViewHolder` example:

	public class ForumViewHolder implements ViewHolder<CCForum> {

	    private TextView dateText;
	    private TextView descriptionText;
	    private TextView userText;

	    private SimpleDateFormat format;

	    @Override
	    public void create(View rowView) {
	        dateText = (TextView)rowView.findViewById(R.id.forumRow_dates);
	        descriptionText = (TextView)rowView.findViewById(R.id.forumRow_titles);
	        userText = (TextView)rowView.findViewById(R.id.forumRow_user);

	        format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    }

	    @Override
	    public void formatHolder(ReusableArrayAdapter<CCForum> adapter, int position) {
	        CCForum forum = adapter.getItem(position);

	        dateText.setText(format.format(forum.date));
	        descriptionText.setText(forum.title);
	        userText.setText(forum.userName);
	    }
	}

`ReusableArrayAdapter2` provides ArrayAdapter with `UITableView`-like behaviour:

	public ReusableArrayAdapter2(Context context, ArrayAdapterDelegate<T> delegate)

`ArrayAdapterDelegate` contains familiar abstract methods:

	public abstract int sectionCount();
    public abstract int rowsInSectionAtPosition(int position);
    public abstract View headerViewForSection(View header, int section);
    public abstract View viewForPosition(View header, int section, int postion);	

sections implemented via adding additional items to the adapter. Does not contain reusability, so you have to recreate sections and cells for each iteration or implement reusability.

`ViewPagerAdapter` provides `ViewPagerHolder` with convinient interfaces:

	ViewPagerAdapter<CCPhoto> adapter =
        new ViewPagerAdapter<CCPhoto>(photos, new ViewPagerHolder<CCPhoto>() {
            @Override
            public View createView(CCPhoto item) {
                return new NewsPhotoView(NewsPhotoActivity.this, item);
            }
        });

    ViewPager pager = (ViewPager)findViewById(R.id.newsPhoto_pager);
    pager.setAdapter(adapter);	

## Unit testing ##

`AsyncStubbedTest` is wrapper for async android junit tests through `CountDownLatch`:

	runAsyncTestMainThread(2, new TestHelper.AsyncTest() {
        @Override
        public void performTest(final CountDownLatch latch) {
        	//asert smting                
            latch.countDown();                
        }
    });

## Usage ##

Add project to the workspace as library project. Some features are barelly tested and contains only basic implementation.

## License ##

(The MIT License)