/**
 * Copyright 2005-2015 titilink
 *
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 *
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 *
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 *
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 *
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 *
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 *
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 *
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * https://github.com/titilink/titilink-framework
 *
 * titilink is a registered trademark of titilink.inc
 */
package com.titilink.common.log;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 日志积累到20M时，调用打包类，将日志打包压缩
 * <p>
 * @author by kam
 * @date 2015/04/29
 * @since v1.0.0
 */
public final class SizeRollingZipFileAppender extends RollingFileAppender {

    private static final String ZIP_SUFFIX = ".zip";
    private final int BUFFER_SIZE = 1024 * 10;
    /**
     * 文件的后缀名
     */
    private long nextRollover = 0;
    private String logFileName = null;
    private String logFileAttachName = null;

    /**
     * 获得文档备份时的时间
     *
     * @return 获取的时间
     */
    private static String getDateTime() {
        String timeString;
        Date dateTime = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        timeString = df.format(dateTime);
        return timeString;
    }

    /**
     * 获得指定生成日志的后缀名
     *
     * @param fileName log4j中配置日志的文件名
     * @return 文件名后缀
     */
    private void getFileAttach(String fileName) {
        int endPos = fileName.lastIndexOf(".");
        if (-1 == endPos) {
            logFileAttachName = "";
            logFileName = fileName;
            return;
        }
        logFileAttachName = fileName.substring(endPos);
        logFileName = fileName.substring(0, endPos);
    }

    /**
     * 获得文件备份的编号
     *
     * @param file 备份文件
     * @return 文件备份编号
     */
    private static String getFileIndex(File file) {
        String nameIdx = null;
        String fileName = null;
        if (null == file || !file.exists()) {
            return nameIdx;
        }
        fileName = file.getName();
        int nStart = fileName.indexOf("[");
        int nEnd = fileName.indexOf("]", nStart);
        if (nEnd < nStart) {
            return null;
        }
        if (nStart == -1 || nEnd == -1) {
            return null;
        }
        nameIdx = fileName.substring(nStart + 1, nEnd);
        return nameIdx;
    }

    /**
     * 通过正则表达式寻找匹配的文件修改备份文件编号
     *
     * @param logFileName       文件名（无后缀）
     * @param logFileAttachName 文件后缀
     * @param fileName          文件路径+文件名
     * @return 是否取到文件
     */
    private static boolean isMatchLogFileName(String logFileName, String logFileAttachName, String fileName) {
        if (null == logFileName || null == logFileAttachName || null == fileName) {
            LogLog.debug("file head is wrong");
            return false;
        }
        Pattern pattern = null;
        boolean isMatch = true;
        int filePos = logFileName.lastIndexOf("/");
        String fileRealName = null;
        if (-1 == filePos) {
            fileRealName = logFileName;
        } else {
            fileRealName = logFileName.substring(filePos + 1);
        }
        String logFileMatchString =
                fileRealName + "\\.\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.\\[\\d{1,3}\\]" + logFileAttachName
                        + ZIP_SUFFIX;
        logFileMatchString = Normalizer.normalize(logFileMatchString, Normalizer.Form.NFKC);
        try {
            pattern = Pattern.compile(logFileMatchString);
            Matcher match = pattern.matcher(fileName);
            isMatch = match.matches();
        } catch (PatternSyntaxException e) {
            return false;
        }
        return isMatch;
    }

    /**
     * 获得指定路径下文件名匹配的文件
     *
     * @param dir         指定路径
     * @param logFileName 匹配的字符串
     * @return 获得到的文件
     */
    private static List<File> getlistLog(File dir, String logFileName, String logFileAttachName) {
        boolean isMatch = false;
        if (null == dir) {
            LogLog.debug("dir is null");
            return null;
        }
        File[] fs = dir.listFiles();
        if (fs.length == 0) {
            LogLog.debug("dir length is 0");
            return null;
        }
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < fs.length; i++) {
            isMatch = isMatchLogFileName(logFileName, logFileAttachName, fs[i].getName());
            if (isMatch) {
                String nameIndex = getFileIndex(fs[i]);
                if (null == nameIndex) {
                    continue;
                }
                if (fs[i].isDirectory()) {
                    continue;
                }
                fileList.add(fs[i]);
            }
        }
        return fileList;
    }

    /**
     * 获得文件备份编号大于指定备份数的文件
     *
     * @param logFile 指定路径
     * @param maxNum  指定备份数
     * @return 获得文件名数组
     */
    private static String[] getMaxIdxFileName(List<File> logFile, int maxNum, String logFileName,
                                              String logFileAttachName) {
        boolean isMatch = false;
        String[] nullsTtring = null;
        if (null == logFile) {
            return nullsTtring;
        }
        List<String> outFileList = new ArrayList<String>();
        for (int i = 0; i < logFile.size(); i++) {
            isMatch = isMatchLogFileName(logFileName, logFileAttachName, logFile.get(i).getName());
            if (isMatch) {
                String nameIndex = getFileIndex(logFile.get(i));
                if (null == nameIndex) {
                    continue;
                }
                int fileNum;
                try {
                    fileNum = Integer.parseInt(nameIndex);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (fileNum >= maxNum) {
                    outFileList.add(logFile.get(i).getName());
                }
            }
        }
        String[] outName = new String[outFileList.size()];
        for (int i = 0; i < outFileList.size(); i++) {
            outName[i] = outFileList.get(i);
        }
        return outName;
    }

    /**
     * 获得文件所在路径
     *
     * @param fileName 文件路径下的文件
     * @return
     * @throws java.io.IOException
     */
    private static String getLogDir(String fileName) throws IOException {
        if (null == fileName) {
            LogLog.debug("No file to find dir");
            return null;
        }
        File f = new File(fileName);
        File fileParent = null;
        if (null == f.getAbsoluteFile().getParentFile()) {
            LogLog.debug("this parents file error ");
            return null;
        }
        fileParent = f.getAbsoluteFile().getParentFile();
        return fileParent.getAbsolutePath();
    }

    /**
     * 删除过期的文件
     *
     * @param logFile  文件集合
     * @param maxNum   最多的文件数
     * @param filePath 文件的路径
     * @return 文件删除是否成功
     */
    private boolean deleteOutDateFile(List<File> logFile, int maxNum, String filePath) {
        boolean renameSucceeded = true;
        File file = null;
        String[] maxFileName = getMaxIdxFileName(logFile, maxNum, logFileName, logFileAttachName);
        if (null == maxFileName) {
            LogLog.debug("have no dir to get the file");
            return true;
        }
        for (int i = 0; i < maxFileName.length; i++) {
            if (null != maxFileName[i]) {
                file = new File(filePath + "/" + maxFileName[i]);
                if (file.exists()) {
                    renameSucceeded = file.delete();
                }
            }
        }
        return renameSucceeded;
    }

    /**
     * 更换文件名
     *
     * @param filePath log 下所获得的日志及备份文件
     * @param logFiles 原始日志名
     * @return
     */
    private boolean changeFileName(String filePath, List<File> logFiles) {
        boolean renameSucceeded = true;
        File target = null;
        File file = null;
        String index = null;
        int numIdx = 0;
        String addIdxFileName = null;
        for (int i = logFiles.size() - 1; i >= 0; i--) {
            file = logFiles.get(i);
/**
 * 备份文件的编号不存在，不处理该文件
 */
            index = getFileIndex(logFiles.get(i));
            if (null == index) {
                continue;
            }
            try {
                numIdx = Integer.parseInt(index);
            } catch (NumberFormatException e) {
                continue;
            }
            addIdxFileName = file.getName();
            int nStart = addIdxFileName.indexOf(".[");
            if (0 > nStart) {
                continue;
            }
/**
 * 修改备份文件的编号
 */
            String logNameAttch = addIdxFileName.substring(0, nStart);
            if (file.exists()) {
                numIdx += 1;
                target = new File(filePath + "/" + logNameAttch + ".[" + numIdx + "]" + logFileAttachName + ZIP_SUFFIX);
                LogLog.debug("Renaming file " + file + " to " + target);
                renameSucceeded = file.renameTo(target);
            }
        }
        return renameSucceeded;
    }

    /**
     * 该方法为空方法，不能删除，否则程序会进入父进程的该方法
     */
    public void rollOver() {
    }

    /**
     * 判断日志名和日志后缀是否为空
     * <功能详细描述>
     *
     * @see [类、类#方法、类#成员]
     */
    public void initFileName() {
        if (null == logFileName || null == logFileAttachName) {
            getFileAttach(super.fileName);
        }
    }

    /**
     * 更新每次文件备份编号，将备份文件按备份的时间重新命名，控制备份文件的数量。
     * 此方法是重写RollingFileAppender中的rollOver方法。
     */
    public void rollOverDate() {
        initFileName();
        if (qw != null) {
            long size = ((CountingQuietWriter) qw).getCount();
            LogLog.debug("rolling over count=" + size);
// if operation fails, do not roll again until
// maxFileSize more bytes are written
            nextRollover = size + maxFileSize;
        }
        LogLog.debug("maxBackupIndex=" + maxBackupIndex);
        boolean renameSucceeded = true;
// If maxBackups <= 0, then there is no file renaming to be done.
        if (maxBackupIndex > 0) {
// Delete the oldest file, to keep Windows happy.
            String filePath = null;
            try {
                filePath = getLogDir(super.fileName);
            } catch (IOException e1) {
                filePath = null;
                LogLog.debug("IOException:log file path is null!");
                return;
            }
            if (null == filePath) {
                LogLog.debug("No such file dir");
                return;
            }
            File dir = new File(filePath);
            List<File> logFiles = getlistLog(dir, logFileName, logFileAttachName);
            if (null == logFiles) {
                LogLog.debug("No log file!");
                return;
            }
            /**
             * 删除过期的文件
             */
            renameSucceeded = deleteOutDateFile(logFiles, maxBackupIndex, filePath);
            if (!renameSucceeded) {
                LogLog.debug("delete failed.");
                return;
            }
            /**
             * 给文件改名
             */
            renameSucceeded = changeFileName(filePath, logFiles);
            if (renameSucceeded) {
                renameSucceeded = zipit();
            }
        }
        if (renameSucceeded) {
            try {
                this.setFile(fileName, false, bufferedIO, bufferSize);
                nextRollover = 0;
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) //NOPMD
                {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("setFile(logfile, false) call failed.", e);
            }
        }
    }

    private boolean zipit() {
        boolean renameSucceeded;
        File target = null;
        File file = null;
        File zipTarget = null;
        target = new File(logFileName + "." + getDateTime() + logFileAttachName);
        zipTarget =
                new File(logFileName + "." + getDateTime() + ".[" + 1 + "]" + logFileAttachName + ZIP_SUFFIX);
        this.closeFile(); // keep windows happy.
        file = new File(fileName);
        LogLog.debug("Renaming file " + file + " to " + target);
        renameSucceeded = file.renameTo(target);

        if (!renameSucceeded) {
            try {
                this.setFile(fileName, true, bufferedIO, bufferSize);
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) //NOPMD
                {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("setFile(" + fileName + ", true) call failed.");
            }
        }
        try {
            LogLog.debug("ziping file " + target + " to " + zipTarget);
            zip(target, zipTarget);
        } catch (IOException e1) {
            LogLog.error("zip file(" + fileName + ") failed.");
        }
        if (target.exists()) {
            renameSucceeded = target.delete();
        } else {
            renameSucceeded = false;
        }
        return renameSucceeded;
    }

    /**
     * subAppend
     *
     * @param event 日志事件
     */
    protected void subAppend(LoggingEvent event) {
        super.subAppend(event);
        if (super.fileName != null && super.qw != null) {
            long size = ((CountingQuietWriter) super.qw).getCount();
            if (size >= maxFileSize && size >= nextRollover) {
                rollOverDate();
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param sourceFile 源文件
     * @param zipFile    目标文件
     * @throws java.io.IOException 出错时，抛出此异常
     */
    private void zip(File sourceFile, File zipFile) throws IOException {
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new IOException("Error sourceFile prepare to zip: " + sourceFile);
        }
        BufferedInputStream bis = null;
        ZipOutputStream out = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFile), BUFFER_SIZE);
            FileOutputStream dest = new FileOutputStream(zipFile);
            out = new ZipOutputStream(new BufferedOutputStream(dest, BUFFER_SIZE));
            ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
            out.putNextEntry(zipEntry);
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                out.write(data, 0, count);
            }
        } finally {
            if (null != bis) {
                bis.close();
            }
            if (null != out) {
                out.close();
            }
        }
    }

}
