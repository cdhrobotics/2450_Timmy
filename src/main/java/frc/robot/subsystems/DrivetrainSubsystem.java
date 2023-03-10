// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.sensors.Pigeon2;

import edu.wpi.first.hal.simulation.RoboRioDataJNI;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.WindChillSwerveModule;

public class DrivetrainSubsystem extends SubsystemBase {
  private final Pigeon2 gyro;
  private WindChillSwerveModule[] swerveModules;

  /** Creates a new ExampleSubsystem. */
  public DrivetrainSubsystem() {

    gyro = new Pigeon2(Constants.pigeonID);
    gyro.configFactoryDefault();
    zeroGyro();

    swerveModules = new WindChillSwerveModule[] {
      new WindChillSwerveModule(0, Constants.FrontLeftModule.constants),
      new WindChillSwerveModule(1, Constants.FrontRightModule.constants),
      new WindChillSwerveModule(2, Constants.BackLeftModule.constants),
      new WindChillSwerveModule(3, Constants.BackRightModule.constants)
    };

    Translation2d m_frontLeftLocation = new Translation2d(Constants.wheelBase / 2.0, Constants.trackWidth / 2.0);
    Translation2d m_frontRightLocation = new Translation2d(Constants.wheelBase / 2.0, Constants.trackWidth / 2.0);
    Translation2d m_backLeftLocation = new Translation2d(-Constants.wheelBase / 2.0, Constants.trackWidth / 2.0);
    Translation2d m_backRightLocation = new Translation2d(-Constants.wheelBase / 2.0, -Constants.trackWidth / 2.0);


// Creating my kinematics object using the module locations
SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
  m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation
);

    SwerveDriveOdometry swerveOdometry = new SwerveDriveOdometry(
      Constants.swerveKinematics,
      getRotation2d(),
      getModulePositions(),
      new Pose2d(0, 0, new Rotation2d(0))
    );
  }

  public void drive(Translation2d translation, double rotation) {
    SwerveModuleState[] swerveModuleStates = Constants.swerveKinematics.toSwerveModuleStates(
        new ChassisSpeeds(translation.getX(), translation.getY(), rotation)
      );

    for (WindChillSwerveModule mod : swerveModules) {
      mod.setDesiredState(swerveModuleStates[mod.moduleNumber]);
    }
  }

  public void autonomousDrive(double xSpeed, double ySpeed, double rotation) {
    SwerveModuleState[] swerveModuleStates = Constants.swerveKinematics.toSwerveModuleStates(
        new ChassisSpeeds(xSpeed, ySpeed, rotation)
      );

    for (WindChillSwerveModule mod : swerveModules) {
      mod.setDesiredState(swerveModuleStates[mod.moduleNumber]);
    }
  }

  public Rotation2d getRotation2d() {
    Rotation2d rotation2d = Rotation2d.fromDegrees(gyro.getYaw());

    return rotation2d;
  }

  public SwerveModulePosition[] getModulePositions(){
    SwerveModulePosition[] positions = new SwerveModulePosition[4];
    for(WindChillSwerveModule mod : swerveModules){
        positions[mod.moduleNumber] = mod.getPosition();
    }
    return positions;
}

  public double getFrontLeftEncoderVal(){
      double frontLeftEncoderVal = swerveModules[0].getDriveEncoder();
      
      return frontLeftEncoderVal;
    }

  public void zeroGyro() {
    gyro.setYaw(0);
  }


  public boolean exampleCondition() {

    return false;
  }

  @Override
  public void periodic() {
    for (WindChillSwerveModule mod : swerveModules) {
      SmartDashboard.putNumber(
          "Mod " + mod.moduleNumber + " Cancoder", mod.getCanCoder().getDegrees());
      SmartDashboard.putNumber(
          "Mod " + mod.moduleNumber + " Integrated", mod.getState().angle.getDegrees());
      SmartDashboard.putNumber(
          "Mod " + mod.moduleNumber + " Velocity", mod.getState().speedMetersPerSecond);
      
      // SmartDashboard.putNumber("Average Encoder Value", getAverageEncoderVal());
    }

  }

  @Override
  public void simulationPeriodic() {

  }
}
