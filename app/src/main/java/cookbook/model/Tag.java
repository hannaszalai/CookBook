package cookbook.model;

/*
 * Model class for Tags.
 */
public class Tag {
	private int id;
	private String name;
	private boolean predefinedTagFlag;

	public Tag(){
	}

	public Tag(int id, String name, boolean predefinedTagFlag) {
		this.id = id;
		this.name = name;
		this.predefinedTagFlag = predefinedTagFlag;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPredefinedTagFlag() {
        return predefinedTagFlag;
    }

    public void setPredefinedTagFlag(boolean predefinedTagFlag) {
        this.predefinedTagFlag = predefinedTagFlag;
    }
}
