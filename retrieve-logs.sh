#!/bin/bash
scp lvuser@10.18.85.2:ODOMETRY.csv .
scp lvuser@10.18.85.2:TRAJECTORY.csv .
scp lvuser@10.18.85.2:SETPOINT.csv .
ssh lvuser@10.18.85.2 "rm ODOMETRY.csv"
ssh lvuser@10.18.85.2 "rm TRAJECTORY.csv"
ssh lvuser@10.18.85.2 "rm SETPOINT.csv"
ssh lvuser@10.18.85.2 "rm VELOCITY_DATA.csv"
ssh lvuser@10.18.85.2 "rm ACCEL_DATA.csv"