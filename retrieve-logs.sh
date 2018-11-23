#!/bin/bash

echo Removing locally stored logs...
rm *.csv

echo Retrieving most recent logs from robot...
scp lvuser@10.18.85.2:*.csv .

echo Cleaning up log files on robot...
ssh lvuser@10.18.85.2 "rm *.csv"