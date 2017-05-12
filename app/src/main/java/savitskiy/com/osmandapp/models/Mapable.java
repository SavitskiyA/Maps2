package savitskiy.com.osmandapp.models;



import java.util.List;

public interface Mapable {
	public String getName();
	public void setName(String name);
	public String getType();
	public void setType(String type);
	public String getMap();
	public void setMap(String map);
	public String getDownload_suffix();
	public void setDownload_suffix(String download_suffix);
	public String getDownload_prefix();
	public void setDownload_prefix(String download_prefix);
	public String getInner_download_suffix();
	public void setInner_download_suffix(String inner_download_suffix);
	public String getInner_download_prefix();
	public void setInner_download_prefix(String inner_download_prefix);
	public String getFileName();
	public void setFileName(String fileName);
	public List<Map> getChildRegions();
	public Map getParent();
	public int getMaxSize();
	public void setMaxSize(int maxSize);
	public int getProgressSize();
	public void setProgressSize(int progressSize);
}
