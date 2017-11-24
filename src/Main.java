import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    /**
     * 実行ファイルの存在するディレクトリのパスを返す。
     */
    private static String getCurrentPath() {
        String cp = System.getProperty("java.class.path");
        String fs = System.getProperty("file.separator");
        String acp = (new File(cp)).getAbsolutePath();
        int p, q;
        for (p = 0; (q = acp.indexOf(fs, p)) >= 0; p = q + 1) ;
        return acp.substring(0, p);
    }

    public static void main(String[] args) {
        CreatePDECollectorOutput();
    }

    private static void CreatePDECollectorOutput() {
        File savingDir = new File(getCurrentPath() + "/PDECollectorOutput");
        if (savingDir.exists()) {
            CreateSketchDir(savingDir);
        } else {
            if (savingDir.mkdir()) {
                CreateSketchDir(savingDir);
            } else {
                ShowFaultMessage("PDECollectorOutput ディレクトリの生成に失敗しました。");
            }
        }
    }

    private static void CreateSketchDir(File savingDir) {
        File sketchDir = new File(savingDir + "/" + savingDir.getParentFile().getName());
        if (sketchDir.exists()) {
            CreateSketchFile(sketchDir);
            CopyDataDir(sketchDir);
            ShowFaultMessage("スケッチフォルダの生成が完了しました。");
        } else {
            if (sketchDir.mkdir()) {
                CreateSketchFile(sketchDir);
                CopyDataDir(sketchDir);
                ShowFaultMessage("スケッチフォルダの生成が完了しました。");
            } else {
                ShowFaultMessage("スケッチフォルダの生成に失敗しました。");
            }
        }
    }

    private static void CopyDataDir(File sketchDir) {
        File f = new File(getCurrentPath() + "/data");
        if (!f.exists()) {
            return;
        }

        FileUtility.directoryCopy(f, sketchDir);
    }

    private static void CreateSketchFile(File sketchDir) {
        File sketch = new File(sketchDir + "/" + sketchDir.getName() + ".pde");
        if (sketch.exists()) {
            sketch.delete();
        }
        try {
            if (sketch.createNewFile()) {
                CollectPDEFiles(sketch);
            } else {
                ShowFaultMessage("スケッチファイルの生成に失敗しました。");
            }
        } catch (IOException e) {
            ShowFaultMessage("スケッチファイルの生成に失敗しました。");
        }
    }

    private static void CollectPDEFiles(File sketch) {
        File f = new File(getCurrentPath());
        File[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File _f) {
                if (_f.isDirectory()) {
                    return false;
                }
                return isContain(_f);
            }

            /**
             * 拡張子を抽出する。
             */
            public String getExtension(File f) {
                String ext = null;
                String filename = f.getName();
                int dotIndex = filename.lastIndexOf('.');

                if ((dotIndex > 0) && (dotIndex < filename.length() - 1)) {
                    ext = filename.substring(dotIndex + 1).toLowerCase();
                }
                return ext;
            }

            /**
             * 適切な拡張子であればtrueを、そうでなければfalseを返す。
             */
            boolean isContain(File f) {
                String ext = getExtension(f);
                return ext != null && ext.equals("pde");
            }
        });

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sketch), "UTF-8")));

            for (File file : files) {
                if (sketch.getName().equals(file.getName())) {
                    pw.print(readAll(file.getPath()));
                    break;
                }
            }

            for (File file : files) {
                if (!sketch.getName().equals(file.getName())) {
                    pw.print(readAll(file.getPath()));
                }
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
            ShowFaultMessage("ファイルの書き込みに失敗しました。");
        }
    }

    private static String readAll(final String path) throws IOException {
        return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
            .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    private static void ShowFaultMessage(String message) {
        JFrame f = new JFrame();
        JOptionPane.showMessageDialog(f, message);
        System.exit(1);
    }
}
