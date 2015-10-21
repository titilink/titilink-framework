#!/bin/bash
# 该脚本用于停止 silvan进程
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

##引进公共模块
. ./bin/util.sh

#检查进程是否起来
status silvan.main.proc
STAT=$?
if [ $STAT -ne 0 ]; then
logger "proc has stopped."
exit 1
fi

logger "stopping silvan module..."

pid=$(ps -wwef | grep "silvan.main.proc" | grep -v grep | awk '{print $2}')

kill ${pid}

sleep 5;
#检查进程是否成功关闭
status silvan.main.proc
STAT=$?
if [ $STAT -eq 0 ]; then
   kill -9 ${pid}
   logger "stop silvan forced"
fi

echo "logger silvan success"

exit 0