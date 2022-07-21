#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
#

usage() {
  #echo "$(basename $0): [-a] [-c] [-e] [-o] [-m] [-s] [-d] [-t]"              >&2
  echo "$(basename $0): [-c] [-e] [-o] [-m] [-s] [-d] [-t]"                   >&2
  #echo "  -a : audit trail (extensions/security)"                             >&2
  echo "  -c : command log (extensions/core)"                                 >&2
  echo "  -e : execution log (extensions/core)"                               >&2
  echo "  -o : execution outbox (extensions/core)"                            >&2
  echo "  -m : secman (extensions/security)"                                  >&2
  echo "  -s : session log (extensions/security)"                             >&2
  echo "  -d : demo (examples/demo/domain)"                                   >&2
  echo "  -t : JDO regression tests (regressiontests/stable-persistence-jdo)" >&2
}



AUDITTRAIL=""
COMMANDLOG=""
DEMO=""
EXECUTIONLOG=""
EXECUTIONOUTBOX=""
REGRESSIONTESTS=""
SECMAN=""
SESSIONLOG=""

PATHS=()

#while getopts ":acdeomsht" arg; do
while getopts ":cdeomsht" arg; do
  case $arg in
    h)
      usage
      exit 0
      ;;
#    a)
#      AUDITTRAIL="enhance"
#      PATHS+=( "extensions/security/audittrail/persistence-jdo" )
#      ;;
    c)
      COMMANDLOG="enhance"
      PATHS+=( "extensions/core/commandlog/persistence-jdo" )
      ;;
    e)
      EXECUTIONLOG="enhance"
      PATHS+=( "extensions/core/executionlog/persistence-jdo" )
      ;;
    m)
      SECMAN="enhance"
      PATHS+=( "extensions/security/secman/persistence-jdo" )
      ;;
    o)
      EXECUTIONOUTBOX="enhance"
      PATHS+=( "extensions/core/executionoutbox/persistence-jdo" )
      ;;
    s)
      SESSIONLOG="enhance"
      PATHS+=( "extensions/security/sessionlog/persistence-jdo" )
      ;;
    d)
      DEMO="enhance"
      PATHS+=( "examples/demo/domain" )
      ;;
    t)
      REGRESSIONTESTS="enhance"
      PATHS+=( "regressiontests/stable-persistence-jdo" )
      ;;
    *)
      usage
      exit 1
  esac
done

shift $((OPTIND-1))


echo "AUDITTRAIL      : $AUDITTRAIL"
echo "COMMANDLOG      : $COMMANDLOG"
echo "EXECUTIONLOG    : $EXECUTIONLOG"
echo "EXECUTIONOUTBOX : $EXECUTIONOUTBOX"
echo "SECMAN          : $SECMAN"
echo "SESSIONLOG      : $SESSIONLOG"
echo "DEMO            : $DEMO"
echo "REGRESSIONTESTS : $REGRESSIONTESTS"


printf -v PATHS_SPLATTED '%s,' "${PATHS[@]}"
PL_ARG=$(echo "${PATHS_SPLATTED%,}")

if [ "$REGRESSIONTESTS" = "enhance" ]; then
  PL_ARG="$PL_ARG -Dmodule-all"
fi

if [ "$PL_ARG" = " " ]; then
  usage
  exit 1
fi

echo mvn install -DskipTests -o -T1C -am -pl $PL_ARG
mvn install -DskipTests -o -T1C -am -pl $PL_ARG