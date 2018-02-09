import java.io.File;
import java.io.FileFilter;

public class CollectFilter implements FileFilter {
    public boolean accept(File _f) {
        if (_f.isDirectory()) return false;
        return isContain(_f);
    }

    private String getExtension(File f) {
        String ext = null;
        String filename = f.getName();
        int dotIndex = filename.lastIndexOf('.');

        if ((dotIndex > 0) && (dotIndex < filename.length() - 1)) {
            ext = filename.substring(dotIndex + 1).toLowerCase();
        }
        return ext;
    }

    private boolean isContain(File f) {
        String ext = getExtension(f);
        return ext != null && ext.equals("pde");
    }
}
