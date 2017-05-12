package savitskiy.com.osmandapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Map implements Mapable, Serializable{
	protected String name;
	protected String type;
	protected String map;
	protected String download_suffix;
	protected String download_prefix;
	protected String inner_download_suffix;
	protected String inner_download_prefix;
	protected List<Map>childRegions;
	protected String fileName;
	protected Map parentName;


	protected int maxSize=-1;
	protected int progressSize=-1;

	protected int downloadPosition=-1;

	public int getDownloadPosition() {
		return downloadPosition;
	}

	public void setDownloadPosition(int downloadPosition) {
		this.downloadPosition = downloadPosition;
	}




	public Map(String name, String type, String map, String download_suffix, String download_prefix,
               String inner_download_suffix, String inner_download_prefix, Map parentName) {
		super();
		this.name = name;
		this.type = type;
		this.map = map;
		this.download_suffix = download_suffix;
		this.download_prefix = download_prefix;
		this.inner_download_suffix = inner_download_suffix;
		this.inner_download_prefix = inner_download_prefix;
		this.parentName=parentName;
		childRegions=new ArrayList<>();
	}



}
