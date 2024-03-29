package edu.umbc.cs.ebiquity.heimdall.util;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.umbc.cs.ebiquity.heimdall.R;

public class AppListAdapter extends ArrayAdapter<ApplicationInfo> {
	private List<ApplicationInfo> appList;
	private Context context;
	private PackageManager packageManager;
	
	public AppListAdapter(Context context, int resource, List<ApplicationInfo> objects) {
		super(context, resource, objects);

		this.context = context;
		this.appList = objects;
		packageManager = context.getPackageManager();
	}
	
	@Override
	public int getCount() {
		return ((appList != null) ? appList.size() : 0);
	}

	@Override
	public ApplicationInfo getItem(int position) {
		return 	((appList != null) ? appList.get(position) : null);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.app_list_item, null);
		}
		
		ApplicationInfo data = appList.get(position);
		
		if(data != null) {
			ImageView appIcon = (ImageView) view.findViewById(R.id.app_icon);
			TextView appName = (TextView) view.findViewById(R.id.app_name);
			TextView packageName = (TextView) view.findViewById(R.id.app_package);
			
			appIcon.setImageDrawable(data.loadIcon(packageManager));
			appName.setText(data.loadLabel(packageManager));
			packageName.setText(data.packageName);
		}
		return view;
	}

}