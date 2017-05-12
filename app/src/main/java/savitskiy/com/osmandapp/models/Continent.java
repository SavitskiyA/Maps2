package savitskiy.com.osmandapp.models;

import java.util.List;

public class Continent extends Map {

    public Continent(String name, String type, String map, String download_suffix, String download_prefix,
                     String inner_download_suffix, String inner_download_prefix, Map parentName) {
        super(name, type, map, download_suffix, download_prefix, inner_download_suffix, inner_download_prefix,
                parentName);

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    @Override
    public void setName(String name) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return type;
    }

    @Override
    public void setType(String type) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getMap() {
        // TODO Auto-generated method stub
        return map;
    }

    @Override
    public void setMap(String map) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDownload_suffix() {
        if (this.download_suffix != null) {
            if (this.download_suffix.equalsIgnoreCase("$name")) return this.getName();
        }
        return download_suffix;
    }

    @Override
    public void setDownload_suffix(String download_suffix) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDownload_prefix() {
        if (this.download_prefix != null) {
            if (this.download_prefix.equalsIgnoreCase("$name")) return this.getName();
        }
        return download_prefix;
    }

    @Override
    public void setDownload_prefix(String download_prefix) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getInner_download_suffix() {
        if (this.inner_download_suffix != null) {
            if (this.inner_download_suffix.equalsIgnoreCase("$name")) return this.getName();
        }
        return inner_download_suffix;
    }

    @Override
    public void setInner_download_suffix(String inner_download_suffix) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getInner_download_prefix() {
        if (this.inner_download_prefix != null) {
            if (this.inner_download_prefix.equalsIgnoreCase("$name")) return this.getName();
        }
        return inner_download_prefix;
    }

    @Override
    public void setInner_download_prefix(String inner_download_prefix) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getFileName() {
        // TODO Auto-generated method stub
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        // TODO Auto-generated method stub
        this.fileName = fileName;
    }

    @Override
    public List<Map> getChildRegions() {
        // TODO Auto-generated method stub
        return childRegions;
    }

    @Override
    public Map getParent() {
        // TODO Auto-generated method stub
        return parentName;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int getProgressSize() {
        return progressSize;
    }

    @Override
    public void setProgressSize(int progressSize) {
        this.progressSize = progressSize;
    }


//	@Override
//	public String toString() {
//		// TODO Auto-generated method stub
//		return this.getClass().getTypeName().substring(this.getClass().getTypeName().lastIndexOf(".")+1) + " " + getName()
//				+ " " + getParent().getClass().getTypeName().substring(this.getClass().getTypeName().lastIndexOf(".")+1)+" "+getParent().getName()+" "
//				+ " " + getMap() + " " + getType() + " " + " download_prefix: "+getDownload_prefix()+" download_suffix: "+getDownload_suffix()+" inner_download_preffix: "+getInner_download_prefix()+" inner_download_suffix: "+getInner_download_suffix();
//	}


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.getClass().getSimpleName() + " " + getName()
                + " " + getParent().getClass().getSimpleName() + " "
                + " " + getMap() + " " + getType() + " " + " download_prefix: " + getDownload_prefix() + " download_suffix: " + getDownload_suffix() + " inner_download_preffix: " + getInner_download_prefix() + " inner_download_suffix: " + getInner_download_suffix() + " fileName " + getFileName();
    }


}
