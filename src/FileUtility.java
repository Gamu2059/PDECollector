import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

class FileUtility {
    private static String FILE_SEPARATOR = "\\";

    static Boolean directoryCopy(File dirFrom, File dirTo) {
        File[] fromFile = dirFrom.listFiles();
        dirTo = new File(dirTo.getPath() + FILE_SEPARATOR + dirFrom.getName());

        dirTo.mkdir();

        if (fromFile == null) return true;
        for (File f : fromFile) {
            if (f.isFile()) {
                if (!fileCopy(f, dirTo)) return false;
                continue;
            }
            if (!directoryCopy(f, dirTo)) {
                return false;
            }
        }
        return true;
    }

    private static Boolean fileCopy(File file, File dir) {
        File copyFile = new File(dir.getPath() + FILE_SEPARATOR + file.getName());
        FileChannel channelFrom = null;
        FileChannel channelTo = null;

        try {
            copyFile.createNewFile();
            channelFrom = new FileInputStream(file).getChannel();
            channelTo = new FileOutputStream(copyFile).getChannel();

            channelFrom.transferTo(0, channelFrom.size(), channelTo);

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (channelFrom != null) {
                    channelFrom.close();
                }
                if (channelTo != null) {
                    channelTo.close();
                }

                //更新日付もコピー
                copyFile.setLastModified(file.lastModified());
            } catch (IOException e) {
                return false;
            }
        }
    }
}
