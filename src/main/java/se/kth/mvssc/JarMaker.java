package se.kth.mvssc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * (Permanently) borrowed from https://stackoverflow.com/a/59439786 :)
 */
public class JarMaker {
    /**
     * Make a jar
     *
     * Aside: To create a jar with the `jar` tool, from the cli
     * cd into -spl directory, and `jar cvfM dependency-version-spl.jar ./`
     * cvfM = create verbose (jar)filename no-manifest-file
     * OR stay in dependency directory,
     * `jar cvfM xstream-spl.jar -C xstream-1.4.12-spl .`
     * -C tells jar tool to go into specified directory
     *
     * @param absolutePathOfSpecializedLibraryDirectory
     * @param outputJarName
     * @throws Exception
     */
    public void make(String absolutePathOfSpecializedLibraryDirectory,
                     String outputJarName) throws Exception {
        JarOutputStream target = new JarOutputStream(new FileOutputStream(outputJarName));
        File inputDirectory = new File(absolutePathOfSpecializedLibraryDirectory);
        for (File nestedFile : inputDirectory.listFiles())
            add("", nestedFile, target);
        target.close();
    }

    /**
     * Recursively add files to a jar
     *
     * @param parents
     * @param source
     * @param target
     * @throws Exception
     */
    private void add(String parents, File source, JarOutputStream target) throws Exception {
        BufferedInputStream in = null;
        try {
            String name = (parents + source.getName()).replace("\\", "/");
            if (source.isDirectory()) {
                if (!name.isEmpty()) {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile : source.listFiles())
                    add(name, nestedFile, target);
                return;
            }
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        } finally {
            if (in != null)
                in.close();
        }
    }
}
