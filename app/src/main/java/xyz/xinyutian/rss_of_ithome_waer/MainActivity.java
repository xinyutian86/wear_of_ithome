package xyz.xinyutian.rss_of_ithome_waer;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ProgressBar progressBar;

    private Pull_ListView listView;

    private MyAdapter myAdapter;

    private List<RSSItemBean> beans=new ArrayList<>();

    private List<RSSItemBean> initData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAdapter = new MyAdapter(MainActivity.this,R.layout.card,beans);
        listView=findViewById(R.id.list_view);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);

        listView.setOnRefreshListener(new Pull_ListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNews(News_Counter.count);
                        listView.finishRefresh();
                    }
                }, 2000);
            }
        });

        listView.setOnLoadMoreListener(new Pull_ListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        News_Counter.count+=10;
                        getNews(News_Counter.count);
                    }
                }, 2000);
            }
        });

        progressBar=findViewById(R.id.progressBar);

        getNews(News_Counter.count);
    }

    Handler handler=new Handler(new Handler.Callback() {
        int position;
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    List<Map<String,String>> info=JSON.parseObject(msg.obj.toString(),List.class);
                    for (Map<String,String> res:info) {
                        RSSItemBean rssItemBean=new RSSItemBean();
                        rssItemBean.setTitle(res.get("title"));
                        if (IsOldNews(rssItemBean,initData)){
                            //
                        }else {
                            beans.add(rssItemBean);
                        }
                        initData=beans;
                    }
                    myAdapter.notifyDataSetChanged();
                    listView.finishLoadMore();
                    progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
                    break;
            }
            return true;
        }
    });

    public void getNews(final int count){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String rss="http://it.androideveloper.club/api/res?count="+count;
                System.err.println("服务器："+rss);
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(rss)
                            .get()//默认就是GET请求，可以不写
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(MainActivity.this,"服务异常...",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Message message=new Message();
                            message.obj=response.body().string();
                            message.what=1;
                            handler.sendMessage(message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //通过view获取其内部的组件，进而进行操作
        String text = (String) ((TextView)view.findViewById(R.id.con)).getText();
        //大多数情况下，position和id相同，并且都从0开始
        Intent intent=new Intent(MainActivity.this,NewsDetailActivity.class);
        intent.putExtra("id",MD5Util.MD5(text));
        startActivity(intent);
    }

    public boolean IsOldNews(RSSItemBean item,List<RSSItemBean> list){
        if (list==null){
            return  false;
        }
        for (RSSItemBean info:list) {
            if (info.getTitle().equals(item.getTitle())){
                return true;
            }
        }
        return false;
    }
}
