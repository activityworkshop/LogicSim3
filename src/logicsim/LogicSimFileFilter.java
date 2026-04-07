package logicsim;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

public class LogicSimFileFilter extends FileFilter {

	private final Hashtable<String, LogicSimFileFilter> filters = new Hashtable<>();
	private String description = null;
	private String fullDescription = null;

	public LogicSimFileFilter() {
	}

	public LogicSimFileFilter(String description, String ... extensions) {
		setDescription(description);
		if (extensions != null) {
			for (String extn : extensions) {
				addExtension(extn);
			}
		}
	}

	public boolean accept(File f) {
        if (f == null) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        }
        final String extension = getExtension(f);
        return extension != null && filters.get(extension) != null;
    }

	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			final int dotPos = filename.lastIndexOf('.');
			if (dotPos > 0 && dotPos < filename.length() - 1) {
				return filename.substring(dotPos + 1).toLowerCase();
			}
		}
		return null;
	}

	public void addExtension(String extension) {
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}

	public String getDescription() {
		if (fullDescription == null) {
			if (description == null) {
				StringBuilder sb = new StringBuilder("(");
				// build the description from the extension list
				Enumeration<String> extensions = filters.keys();
				if (extensions != null) {
					sb.append('.').append(extensions.nextElement());
					while (extensions.hasMoreElements()) {
						sb.append(", .").append(extensions.nextElement());
					}
				}
				sb.append(')');
				fullDescription = sb.toString();
			} else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}
}
