package agency.july.config.models;

import static java.lang.String.format;

public class BrowserProps {

    private DriverType type;
    private String file;
    private Integer width;
    private Integer hight;
    
    public DriverType getType() {
		return type;
	}

	public void setType(DriverType type) {
		this.type = type;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHight() {
		return hight;
	}

	public void setHight(Integer hight) {
		this.hight = hight;
	}

	@Override
    public String toString() {
        return new StringBuilder()
                .append( format( "Browser type: %s\n", type ) )
                .append( format( "Browser file: %s\n", file ) )
                .append( format( "Browser width: %s\n", width ) )
                .append( format( "Browser hight: %s\n", hight ) )
                .toString();
    }

}
