#!/bin/bash
# copyright www.titilink.com
# all right reserved, from 1998 to 2015
# 该脚本用于监控silvan进程
# @author ganting
######################################################

#   DESCRIPTION: 切换到当前目录
#   CALLS      : 无
#   CALLED BY  : main
#   INPUT      : 无
#   OUTPUT     : 无
#   LOCAL VAR  : 无
#   USE GLOBVAR: 无
#   RETURN     : 无
#   CHANGE DIR : 无
######################################################################
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

# action
ACTION=$1
shift

# init ret value for exit
RETVAL=0

# ensure action is specficed
[ -z "$ACTION" ] && die "no action is specficed"
logger_without_echo "Action is $ACTION"

# for status
statusInternal()
{
    # do some work, such as send one msg to process
    local pid=$(ps -ww -eo pid,cmd | grep -w "silvan.main.proc" | grep -w java | grep -vwE "grep|vi|vim|tail|cat" | awk '{print $1}' | head -1)
    RETVAL=1
    [ -n "$pid" ] && RETVAL=0
    if [ $RETVAL -eq 0 ]; then
        echo "normal"
        logger "normal"
    else
        echo "abnormal"
        logger "abnormal"
    fi
    return $RETVAL
}

# for start
start()
{
    # do singleton protect
    if status >/dev/null ; then
        echo "process is running, no need to start"
        logger_without_echo "process is running, no need to start"
        return 2
    fi

    RETVAL=0

    # start process
    sh start_silvan.sh
    if [ $? -eq 0 ] ; then
        logger "start success"
    else
        die "start fail"
    fi
}

# for stop
stop()
{
    # do singleton protect
        if status >/dev/null ; then
            logger_without_echo "process is running, try to stop it"
        else
            logger_without_echo "process is not running, no need to stop"
            return 2;
        fi

        RETVAL=0

        # stop process
        sh stop_silvan.sh
        if [ $? -eq 0 ] ; then
            logger "stopsuccess"
        else
            die "stop fail"
        fi
}

# for restart
restart()
{
    stop
    start
}

case "$ACTION" in
    start)
    start
    ;;
    stop)
    stop
    ;;
    status)
    statusInternal
    ;;
    restart)
    restart
    ;;
    *)
    die $"Usage: $0 {start|stop|status|restart}"
esac

exit $RETVAL