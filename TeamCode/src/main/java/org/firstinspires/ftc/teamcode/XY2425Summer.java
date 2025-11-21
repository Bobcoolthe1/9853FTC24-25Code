package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import java.util.Base64;

@Autonomous(name = "Graph Based Auto", group = "Autonomous")
public class XY2425Summer extends LinearOpMode {

    // Motor and servo declarations
    private DcMotor backRight = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor frontLeft = null;
    private IMU imu = null;
    private VoltageSensor voltageSensor = null;
    private DcMotorEx EncoderL = null;
    private DcMotorEx EncoderR = null;
    private DcMotorEx EncoderF = null;

    // Starting position of the robot (X, Y coordinates)
    private double currentX = 7.8125;
    private double currentY = 1.66666;
    // Constants (adjust for your robot setup)
    final double WHEEL_DIAMETER_INCHES = 4.0;
    final double TICKS_PER_REVOLUTION = 537.7;
    final double GEAR_RATIO = 1.0;

    // Power variables for mecanum drive wheels
    private double frontLeftPower = 0;
    private double frontRightPower = 0;
    private double backLeftPower = 0;
    private double backRightPower = 0;

    //Initialize all hardware devices from the hardware map.
    private void initializeHardware() {
        // Initialize drive motors
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");

        // Initialize IMU with orientation parameters
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        IMU.Parameters parameters = new IMU.Parameters(orientationOnRobot);
        imu.initialize(parameters);
        telemetry.addData("Status", "IMU Initialized");
        telemetry.update();

        // Encoder
        EncoderL = hardwareMap.get(DcMotorEx.class, "EncoderL");
        EncoderR = hardwareMap.get(DcMotorEx.class, "EncoderR");
        EncoderF = hardwareMap.get(DcMotorEx.class, "EncoderF");
        EncoderL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        EncoderR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        EncoderF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        EncoderL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        EncoderR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        EncoderF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set direction of stuff
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        // Get voltage sensor for battery monitoring
        voltageSensor = hardwareMap.voltageSensor.iterator().next();
    }

    private int initialEncoderX = EncoderL.getCurrentPosition();
    private int initialEncoderY = EncoderF.getCurrentPosition();
    private double Xdis() {
        int encoderDiffX = EncoderL.getCurrentPosition() - initialEncoderX;
        double wheelCircumference = Math.PI * WHEEL_DIAMETER_INCHES;
        double rotations = (double) encoderDiffX / (TICKS_PER_REVOLUTION * GEAR_RATIO);
        return rotations * wheelCircumference;
    }

    private double Ydis() {
        int encoderDiffY = EncoderF.getCurrentPosition() - initialEncoderY;
        double wheelCircumference = Math.PI * WHEEL_DIAMETER_INCHES;
        double rotations = (double) encoderDiffY / (TICKS_PER_REVOLUTION * GEAR_RATIO);
        return rotations * wheelCircumference;
    }

    // Convert degrees to radians for trigonometry
    double theta = Math.toRadians(Yawdegree());

    // Adjust encoder-based displacement to be field-centric
    double adjustedXdis = (Xdis() * Math.cos(theta)) - (Ydis() * Math.sin(theta));
    double adjustedYdis = (Xdis() * Math.sin(theta)) + (Ydis() * Math.cos(theta));

    /**
     * Move the robot to specified (X, Y) coordinates on the field.
     * Uses field-centric calculations to adjust movement correctly.
     * @param targetX target X coordinate
     * @param targetY target Y coordinate
     */
    private void Cords(double targetX, double targetY) {
        // Calculate difference in position using adjusted field-centric values
        double deltaX = targetX - adjustedXdis;
        double deltaY = targetY - adjustedYdis;

        // Calculate angle to target in radians
        double angle = Math.atan2(deltaY, deltaX);

        // Adjust movement components based on field orientation
        double powerX = Math.cos(angle);
        double powerY = Math.sin(angle);

        // Calculate individual motor powers for mecanum drive
        frontLeftPower = powerY + powerX;
        frontRightPower = powerY - powerX;
        backLeftPower = powerY - powerX;
        backRightPower = powerY + powerX;

        // Normalize powers if any exceed 1.0
        double maxPower = Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
        );
        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower /= maxPower;
            backRightPower /= maxPower;
        }

        // Set motor powers to drive towards target
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        // Drive until robot reaches target position
        while (Math.abs(targetX - adjustedXdis) > 0.1 && Math.abs(targetY - adjustedYdis) > 0.1) {
            telemetry.addData("TargetX", targetX);
            telemetry.addData("TargetY", targetY);
            telemetry.addData("CurrentX", adjustedXdis);
            telemetry.addData("CurrentY", adjustedYdis);
            telemetry.update();
        }

        stopMotors(); // Stop all motors after reaching target
        Spin(0);
    }

    /**
     * Get the current yaw angle of the robot in degrees from the IMU.
     * @return yaw angle in degrees
     */
    private double Yawdegree() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    /**
     * Spin the robot in place by a specified degree amount.
     * Positive degrees spin clockwise, negative degrees spin counterclockwise.
     * Stops spinning when within 3 degrees of target.
     * @param degree target spin angle in degrees
     */
    private void Spin(double degree) {
        if (degree > 0) {
            // Spin clockwise
            frontLeft.setPower(1);
            frontRight.setPower(-1);
            backRight.setPower(-1);
            backLeft.setPower(1);
        } else if (degree < 0) {
            // Spin counterclockwise
            frontLeft.setPower(-1);
            frontRight.setPower(1);
            backRight.setPower(1);
            backLeft.setPower(-1);
        }

        // Wait until robot reaches target yaw angle within tolerance
        while (Math.abs(Yawdegree()) < (Math.abs(degree) - 3)) {
            telemetry.addData("Spinning", "true");
            telemetry.update();
        }

        stopMotors(); // Stop all drive motors after spinning
    }

    /**
     * Stop all drive motors and pause briefly.
     */
    private void stopMotors() {
        frontLeft.setPower(0);
        backLeft.setPower(0);
        timir(0.5); // Small delay to ensure motors stop
        frontRight.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Simple timer method that waits for specified seconds while updating telemetry.
     * @param time duration to wait in seconds
     */
    private void timir(double time) {
        ElapsedTime timer3 = new ElapsedTime();
        while (opModeIsActive() && timer3.seconds() < time) {
            telemetry.addData("Timer Status", "Running");
            telemetry.update();
        }
    }

    /**
     * Main autonomous routine.
     * Initializes hardware and mechanisms, waits for start,
     * then performs a sequence of scoring and intake cycles.
     */
    @Override
    public void runOpMode() {
        initializeHardware();    // Initialize all hardware devices

        waitForStart();         // Wait for start command

        while (opModeIsActive()) {

            telemetry.addData("X = ",adjustedXdis);
            telemetry.addData("Y = ",adjustedYdis);
            telemetry.addData("Yaw = ",imu.getRobotYawPitchRollAngles().getYaw());
            telemetry.update();


        }
    }
}