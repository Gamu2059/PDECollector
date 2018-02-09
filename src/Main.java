import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.stream.Collectors;

public class Main {
    private boolean isContainData;

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
        new Main();
    }

    public Main() {
        CreatePDECollectorOutput();
    }

    private void CreatePDECollectorOutput() {
        JFrame f = new JFrame();
        int result = JOptionPane.showConfirmDialog(f, "dataフォルダを含めますか？");
        if (result == JOptionPane.CANCEL_OPTION) {
            ShowFaultMessage("出力をキャンセルします。");
        }
        isContainData = result == JOptionPane.YES_OPTION;

        File savingDir = new File(getCurrentPath() + "/web-export");
        if (savingDir.exists()) {
            CreateSketchDir(savingDir);
            return;
        }
        if (savingDir.mkdir()) {
            CreateSketchDir(savingDir);
        } else {
            ShowFaultMessage("web-export ディレクトリの生成に失敗しました。");
        }
    }

    private void CreateSketchDir(File savingDir) {
        try {
            CreateSketchFile(savingDir);
            if (isContainData) {
                CopyDataDir(savingDir);
            }
            ShowFaultMessage("出力が完了しました");
        } catch (Exception e) {
            ShowFaultMessage("出力に失敗しました");
        }
    }

    private void CopyDataDir(File sketchDir) {
        File f = new File(getCurrentPath() + "/data");
        if (!f.exists()) {
            return;
        }

        FileUtility.directoryCopy(f, sketchDir);
    }

    private void CreateSketchFile(File sketchDir) {
        File current = new File(getCurrentPath());
        File sketch = new File(sketchDir + "/" + current.getName() + ".pde");
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

    private void CollectPDEFiles(File sketch) {
        File f = new File(getCurrentPath());
        File[] files = f.listFiles(new CollectFilter());

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sketch), "UTF-8")));

            printMainFile(sketch, files, pw);
            printOtherFile(sketch, files, pw);

            pw.close();
        } catch (IOException e) {
            e.getStackTrace();
            ShowFaultMessage("ファイルの書き込みに失敗しました。");
        }
    }

    private void printMainFile(File sketch, File[] files, PrintWriter pw) throws IOException {
        for (File file : files) {
            if (!sketch.getName().equals(file.getName())) continue;
            pw.println(readAll(file.getPath()));
            break;
        }
    }

    private void printOtherFile(File sketch, File[] files, PrintWriter pw) throws IOException {
        for (File file : files) {
            if (sketch.getName().equals(file.getName())) continue;
            pw.println(readAll(file.getPath()));
        }
    }

    private String readAll(final String path) throws IOException {
        return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
            .collect(Collectors.joining(System.getProperty("line.separator")));
    }

    private static void ShowFaultMessage(String message) {
        JFrame f = new JFrame();
        JOptionPane.showMessageDialog(f, message);
        System.exit(1);
    }
}
