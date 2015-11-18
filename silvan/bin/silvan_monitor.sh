#!/bin/bash
# 该脚本用于监控silvan进程
# @author kam
######################################################
getCurPath()
{
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
        logger "stop success"
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

exit ${RETVAL}