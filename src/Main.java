import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Main {

    // Константа - массив путей к папкам 1-го уровня
    private static final String[] ROOT_FOLDERS = {"src/A", "src/B"};
    private static final double  MAX_DEVIATION = 0.1;

    private static final int N = 2;


    public static void main(String[] args) {
        // Список путей к конечным папкам с файлами .js (уровень 1)
        List<String> jsFoldersLevel1 = new ArrayList<>();

        // Список путей к папкам с количеством файлов .js (уровень 2)
        List<JsFolderInfo> jsFoldersLevel2 = new ArrayList<>();

        // Перебор папок 1-го уровня
        for (String rootFolder : ROOT_FOLDERS) {
            findJsFoldersRecursive(rootFolder, jsFoldersLevel1, jsFoldersLevel2);
        }

        // Печать результатов
        System.out.println("Уровень 1:");
        for (String jsFolder : jsFoldersLevel1) {
            System.out.println(jsFolder);
        }

        System.out.println("\nУровень 2:");
        for (JsFolderInfo jsFolderInfo : jsFoldersLevel2) {
            System.out.println(jsFolderInfo.getPath() + " (" + jsFolderInfo.getJsFileCount() + ")");
        }

        List<List<String>> jsFoldersPartitions = splitList(jsFoldersLevel1, 3);

        System.out.println("\nУровень 3:");
        for (int i = 0; i < jsFoldersPartitions.size(); i++) {
            System.out.println("Часть " + (i + 1) + ":");
            for (String jsFolder : jsFoldersPartitions.get(i)) {
                // Получение количества файлов со 2 уровня
                final int[] k = {0};
                jsFoldersLevel2.stream().anyMatch(x-> {
                    if(Objects.equals(x.getPath(), jsFolder)){
                        k[0] = x.getJsFileCount();
                    }

                    return false;
                });


                System.out.println(jsFolder + " (" + k[0] + ")");
            }
        }
    }

    private static void findJsFoldersRecursive(String folderPath, List<String> jsFoldersLevel1, List<JsFolderInfo> jsFoldersLevel2) {
        File folder = new File(folderPath);

        // Проверка, является ли папка каталогом
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();

            // Перебор файлов и вложенных папок
            for (File file : files) {
                if (file.isDirectory()) {
                    findJsFoldersRecursive(file.getAbsolutePath(), jsFoldersLevel1, jsFoldersLevel2);
                } else if (file.isFile() && file.getName().endsWith(".js")) {
                    // Добавление пути к папке с файлом .js (уровень 1)
                    if (!jsFoldersLevel1.contains(folderPath)) {
                        jsFoldersLevel1.add(folderPath);
                    }

                    // Подсчет файлов .js в папке (уровень 2)
                    int jsFileCount = 0;
                    for (File fileInFolder : folder.listFiles()) {
                        if (fileInFolder.isFile() && fileInFolder.getName().endsWith(".js") ) {
                            jsFileCount++;
                        }
                    }
                    JsFolderInfo jsFolderInfo1 = new JsFolderInfo(folderPath, jsFileCount);

                    if (jsFoldersLevel2.stream().noneMatch(x-> x.getPath() == folderPath)) {
                        jsFoldersLevel2.add(jsFolderInfo1);
                    }

                }
            }
        }
    }
    private static <T> List<List<T>> splitList(List<T> list, int N) {
        // Если N=1 возвращаем список
        if (N <= 1) {
            List<List<T>> partitions = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                partitions.add(new ArrayList<>(list));
            }
            return partitions;
        }

        // Подсчет среднего значения
        double average = (double) list.size() / N;

        // Инициализация списка разделов
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            partitions.add(new ArrayList<>());
        }

        // Сортировка списка по возрастанию
        Collections.sort((List <String> )list);

        // Распределение элементов по разделам
        int i = 0;
        for (T element : list) {
            partitions.get(i).add(element);

            // Переход к следующему разделу, если отклонение от среднего значения выходит за пределы
            double currentDeviation = Math.abs(partitions.get(i).size() / average - 1);
            if (currentDeviation > MAX_DEVIATION) {
                i = (i + 1) % N;
            }
        }

        return partitions;
    }
}

class JsFolderInfo {
    private String path;
    private int jsFileCount;

    public JsFolderInfo(String path, int jsFileCount) {
        this.path = path;
        this.jsFileCount = jsFileCount;
    }

    public String getPath() {
        return path;
    }

    public int getJsFileCount() {
        return jsFileCount;
    }
}