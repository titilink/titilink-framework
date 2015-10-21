#!/bin/bash
# 该脚本用于启动silvan进程
# @author kam
######################################################

getCurPath()
{
    # 1。如果当前目录就是install文件所在位置，直接pwd取得绝对路径；
    # 2。而如果是从其他目录来调用install的情况，先cd到install文
    #    件所在目录,再取得install的绝对路径，并返回至原目录下。
    # 3。使用install调用该文件，使用的是当前目录路径
    if [ "` dirname "$0" `" = "" ] || [ "` dirname "$0" `" = "." ] ; then
        CURRENT_PATH="`pwd`"
    else
        cd ` dirname "$0" `
        CURRENT_PATH="`pwd`"
        cd - > /dev/null 2>&1
    fi
}

getCurPath
cd "${CURRENT_PATH}/.."

# 引入公共模块
. ./bin/util.sh

#检查进程是否起来
status silvan.main.proc
STAT=$?
if [ $STAT -eq 0 ]; then
    logger "proc has started."
    exit 1
fi

classpath1=.
#本模块的jar
for jarList in $(ls -1 lib/*.jar); do
    classpath1=$classpath1:$jarList
done

# 启动函数
logger "starting silvan..."

java -Xms64m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m -Dname=silvan.main.proc -classpath ${classpath1} com.titilink.silvan.main.SilvanMain 1>/dev/null 2>&1 &

RESULT=$?
if [ "$RESULT" -ne "0" ]; then
   logger "start silvan fail"
   exit 2
else
   logger "start silvan success"
fi

# sleep 3s and check proc num
sleep 3;
procnum=$(ps -wwef | grep "silvan.main.proc" | grep -cv grep)
if [ "$procnum" -ne "1" ]; then
    logger "start proc more than one at the same time.";
    pid=$(ps -wwef | grep "silvan.main.proc" | grep -v grep | awk '{print $2}')
    kill -9 ${pid}
    logger "killed all processes, please run it again."
    exit 2
else
    logger "check startup ok."
fi

exit 0