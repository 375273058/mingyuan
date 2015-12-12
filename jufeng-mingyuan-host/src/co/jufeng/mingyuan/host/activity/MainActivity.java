package co.jufeng.mingyuan.host.activity;
import java.io.File;
import java.util.ArrayList;

import co.jufeng.dl.internal.DLIntent;
import co.jufeng.dl.internal.DLPluginManager;
import co.jufeng.dl.utils.DLUtils;
import co.jufeng.mingyuan.host.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {
	/**
	 * 当前对象
	 */
	private MainActivity mainActivity;
	/**
	 * 插件集合
	 */
	private ArrayList<PluginItem> mPluginItems = new ArrayList<PluginItem>();
	private TextView textView;
	private Button loginButton;
	private Button button2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainActivity = this;
		initView();
		initOnClick();
		initPlugin();
	}
	
	public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String launcherActivityName;
        public String launcherServiceName;

        public PluginItem() {
        }

		@Override
		public String toString() {
			return "PluginItem [packageInfo=" + packageInfo + ", pluginPath="
					+ pluginPath + ", launcherActivityName="
					+ launcherActivityName + ", launcherServiceName="
					+ launcherServiceName + "]";
		}
        
    }
	
	private void initView() {
		textView = (TextView)findViewById(R.id.textView);
		loginButton = (Button)findViewById(R.id.loginButton);
		button2 = (Button)findViewById(R.id.button2);
	}
	
	private void initOnClick() {
		textView.setOnClickListener(mainActivity);
		loginButton.setOnClickListener(mainActivity);
		button2.setOnClickListener(mainActivity);
	}
	
	private void initPlugin() {
        String pluginFolder = Environment.getExternalStorageDirectory() + "/jufeng/plugins/mingyuan";
        File file = new File(pluginFolder);
        file.mkdirs();
        File[] plugins = file.listFiles();
        if (plugins == null || plugins.length == 0) {
           
            return;
        }

        for (File plugin : plugins) {
            PluginItem item = new PluginItem();
            item.pluginPath = plugin.getAbsolutePath();
            item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);
            if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0) {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }
            if (item.packageInfo.services != null && item.packageInfo.services.length > 0) {
                item.launcherServiceName = item.packageInfo.services[0].name;
            }
            mPluginItems.add(item);
            DLPluginManager.getInstance(this).loadApk(item.pluginPath);
        }
    }
	
	
	private long exitTime = 0;
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitTime > 0 && (System.currentTimeMillis() - exitTime) < 2000) {
                finish();
                Process.killProcess(Process.myPid());
                
            }else {
            	show("请再按一次退出");
            	exitTime = System.currentTimeMillis();
                return false;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }
	
	/**
	 * 提示信息
	 * 
	 * @param msg
	 */
	public void show(String msg) {
		try {
			if(!isFinishing())
				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.textView:
			show("点击测试" + mPluginItems.size());
			PluginItem pluginItem = mPluginItems.get(0);
			startPlugin(pluginItem);
			break;
			
		case R.id.loginButton:
			show("点击测试登录插件");
			PluginItem pluginItem1 = mPluginItems.get(1);
			startPlugin(pluginItem1);
			break;
			
		case R.id.button2:
			show("点击测试登录插件");
			PluginItem pluginItem2 = mPluginItems.get(2);
			startPlugin(pluginItem2);
			break;
		default:
			break;
		}
	}
	
	

/**
 * 启动插件
 * @param item
 */
protected void startPlugin(PluginItem item) {
	Toast.makeText(mainActivity, item.launcherActivityName, Toast.LENGTH_SHORT).show();
	DLPluginManager pluginManager = DLPluginManager.getInstance(mainActivity);
	DLIntent dlIntent = new DLIntent(item.packageInfo.packageName, item.launcherActivityName);
	pluginManager.startPluginActivity(mainActivity, dlIntent);
}
	
	
}
