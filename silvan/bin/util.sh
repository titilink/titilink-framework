#!/usr/bin/env bash

umask 0027

SERVICE_USER=hae
MODULE_NAME=silvan

LOGGER_MAXSIZE=5120
LOGGER_PATH=/var/log/${SERVICE_USER}/${MODULE_NAME}
LOGGER_FILE=${LOGGER_PATH}/default.log

status()
{
        ps -ef | grep $1 | grep -v grep 1>/dev/null
        RETVAL=$?
        if [ $RETVAL -eq 0 ]; then
            return 0
        else
            return 1
        fi
}

logger_without_echo()
{
    local logsize=0
    if [ -e "$LOGGER_FILE" ]; then
        logsize=`ls -lk $LOGGER_FILE | awk -F " " '{print $5}'`
    else
        touch $LOGGER_FILE
        chown ${SERVICE_USER}: $LOGGER_FILE
        chmod 600 $LOGGER_FILE
    fi

    if [ "$logsize" -gt "$LOGGER_MAXSIZE" ]; then
        # 每次删除最开始的10000行，约300K
        sed -i '1,10000d' $LOGGER_FILE
    fi
    echo "[` date -d today +\"%Y-%m-%d %H:%M:%S\"`,000] $*" >>$LOGGER_FILE

}

logger()
{
    logger_without_echo $*
    echo "$*"
}

die()
{
    logger "$*"
    exit 1
}

initLogDir()
{
    if [ -z ${LOGGER_PATH} ]; then
        return 0
    else
        mkdir -p ${LOGGER_PATH%/*}
        chown ${SERVICE_USER}: ${LOGGER_PATH%/*}
    fi
}

chkUser()
{
    if [ `whoami` != "${SERVICE_USER}" ]; then
        logger "silvan can only start by hae"
        die
    fi
}


