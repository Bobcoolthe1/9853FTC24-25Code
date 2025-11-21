package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import java.util.Base64;

@Autonomous(name = "Graph Based Auto", group = "Autonomous")
public class GraphBasedAuto2425 extends LinearOpMode {

    // Motor and servo declarations
    private DcMotor backRight = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor frontLeft = null;
    private DcMotor SlideLeft = null;
    private DcMotor SlideRight = null;
    private CRServo intakeFront = null;
    private CRServo intakeBack = null;
    private Servo Bucket = null;
    private Servo Wrist = null;
    private Servo LWrist = null;
    private DcMotor bar = null;
    private Servo ITR = null;
    private TouchSensor LeftSensor = null;
    private TouchSensor RightSensor = null;
    private IMU imu = null;
    private VoltageSensor voltageSensor = null;

    // Starting position of the robot (X, Y coordinates)
    private double currentX = 7.8125;
    private double currentY = 1.66666;

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

        //Encoder




        // Initialize bar motor and configure encoder and brake behavior
        bar = hardwareMap.get(DcMotor.class, "bar");
        bar.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bar.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bar.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Initialize intake servos
        intakeFront = hardwareMap.get(CRServo.class, "intakeFront");
        intakeBack = hardwareMap.get(CRServo.class, "intakeBack");

        // Initialize servos for bucket and wrists
        Bucket = hardwareMap.get(Servo.class, "bucket");
        Wrist = hardwareMap.get(Servo.class, "wrist");
        Wrist.setPosition(0.54); // Set initial wrist position
        LWrist = hardwareMap.get(Servo.class, "wristLeft");
        ITR = hardwareMap.get(Servo.class, "ITR");

        // Initialize slide motors and configure encoders and brake behavior
        SlideLeft = hardwareMap.get(DcMotor.class, "leftbar");
        SlideRight = hardwareMap.get(DcMotor.class, "rightBar");
        SlideLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SlideRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SlideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SlideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Initialize touch sensors for slide limit detection
        RightSensor = hardwareMap.get(TouchSensor.class, "rightBarSensor");
        LeftSensor = hardwareMap.get(TouchSensor.class, "leftBarSensor");

        // Set motor directions for proper movement
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        SlideLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        SlideRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Get voltage sensor for battery monitoring
        voltageSensor = hardwareMap.voltageSensor.iterator().next();
    }

    //Initialize mechanisms to starting positions.

    private void initializeMechanisms() {
        Bucket.setPosition(1); // Bucket fully retracted
        Slidesmove(0, 1.0);    // Move slides to position 0 at full power
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
     * Move the bar motor to a target encoder position at full power.
     * @param targetPosition encoder target position
     */
    private void Barmove(int targetPosition) {
        bar.setTargetPosition(targetPosition);
        bar.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bar.setPower(1);
    }

    /**
     * Move the slide motors to a target encoder position at specified power.
     * @param targetPosition encoder target position
     * @param power motor power (0 to 1)
     */
    private void Slidesmove(int targetPosition, double power) {
        SlideLeft.setTargetPosition(targetPosition);
        SlideRight.setTargetPosition(targetPosition);
        SlideLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        SlideRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        SlideLeft.setPower(power);
        SlideRight.setPower(power);
    }

    /**
     * Calculate the sum of absolute powers of all four drive motors.
     * @return sum of absolute motor powers
     */
    private double WP() {
        return Math.abs(frontLeftPower + frontRightPower + backLeftPower + backRightPower);
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
     * Perform the scoring routine:
     * - Move to scoring position
     * - Spin robot
     * - Raise slides
     * - Deposit game element with bucket servo
     * - Retract slides and bucket
     * - Spin back to initial orientation
     */
    private void Score() {
        Cords(2.5, 2.5); // Move to scoring coordinates
        timir(0.5);       // Wait briefly
        Spin(45);         // Spin 45 degrees clockwise

        Slidesmove(5150, 1.0); // Raise slides to position 5150 at full power
        timir(3);              // Wait for slides to reach position

        Bucket.setPosition(0.4); // Open bucket to deposit
        sleep(1000);             // Wait 1 second for deposit

        Bucket.setPosition(0.8); // Close bucket partially
        Slidesmove(-200, 0.75);  // Lower slides slightly
        timir(1);                // Wait 1 second

        // If either slide limit sensor is pressed, reset slide encoders and stop slides
        if (LeftSensor.isPressed() || RightSensor.isPressed()) {
            SlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            SlideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            SlideLeft.setPower(0);

            SlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            SlideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            SlideRight.setPower(0);

            Bucket.setPosition(1); // Fully close bucket
        }
        timir(3); // Wait 3 seconds

        Spin(0); // Spin back to 0 degrees orientation
    }

    /**
     * Move the robot to specified (X, Y) coordinates on the field.
     * Calculates direction and distance, sets motor powers accordingly,
     * and drives until target distance is reached.
     * @param targetX target X coordinate
     * @param targetY target Y coordinate
     */
    private void Cords(double targetX, double targetY) {
        // Calculate difference in position
        double deltaX = targetX - currentX;
        double deltaY = targetY - currentY;

        // Calculate distance to target, scaled by 0.4 (tuning factor)
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY) * 0.4;

        // Calculate angle to target in radians
        double angle = Math.atan2(deltaY, deltaX);

        // Calculate power components for mecanum drive
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

        // Reset front left motor encoder and set to run using encoder mode
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Calculate rotations needed based on distance and power
        double Rotations_Per_Foot = 1200 * Math.abs(frontLeftPower);
        double AddRotation = distance * Rotations_Per_Foot;

        // Calculate target encoder position for front left motor
        double TargetRotations = frontLeft.getCurrentPosition() + (frontLeftPower < 0 ? AddRotation : -(AddRotation));

        // Set motor powers to drive towards target
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        // Drive until front left motor reaches target encoder position
        while (Math.abs(frontLeft.getCurrentPosition()) < Math.abs(TargetRotations)) {
            telemetry.addData("AddRotations", AddRotation);
            telemetry.addData("Rotations_Per_Foot", Rotations_Per_Foot);
            telemetry.addData("TargetRotations", Math.abs(TargetRotations));
            telemetry.update();
        }

        stopMotors(); // Stop all motors after reaching target

        // Update current position tracking
        if (targetX == -0.2 && targetY == 1) {
            currentX = 0;
            currentY = 2.6666;
        } else {
            currentX = targetX;
            currentY = targetY;
        }

        timir(0.3); // Small delay after movement

        /*
        // Commented out code for yaw correction after movement
        while (Math.abs(imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES)) > 1.5); {
            telemetry.addData("YawRunning", "true");
            telemetry.addData("degree", Yawdegree());
            telemetry.update();

            if (Yawdegree() > 0) {
                frontRight.setPower(-0.5);
                backRight.setPower(-0.5);
                frontLeft.setPower(0.5);
                backLeft.setPower(0.5);
            } else {
                frontRight.setPower(0.5);
                backRight.setPower(0.5);
                frontLeft.setPower(-0.5);
                backLeft.setPower(-0.5);
            }
        }
        // Ensure the motors stop after exiting the loop
        stopMotors();
        */
    }

    /**
     * Perform the intake macro routine:
     * - Move wrists to intake position
     * - Run intake motors to collect game elements
     * - Move bar motor to positions for intake
     * - Reset bar encoder if needed
     * - Run intake motors to eject game elements
     */
    private void IntakeMacro() {
        LWrist.setPosition(0.75); // Move left wrist to intake position
        timir(0.7);               // Wait 0.7 seconds

        Wrist.setPosition(0.2);   // Move wrist to intake position
        Intake(-1);               // Run intake motors inward
        timir(0.5);               // Wait 0.5 seconds

        Barmove(275);             // Move bar motor to position 275
        timir(0.5);               // Wait 0.5 seconds

        Barmove(300);             // Move bar motor to position 300
        timir(0.75);              // Wait 0.75 seconds

        Intake(0);                // Stop intake motors
        Barmove(-50);             // Move bar motor back slightly
        Wrist.setPosition(0.54);  // Reset wrist position
        timir(0.5);               // Wait 0.5 seconds

        LWrist.setPosition(0);    // Reset left wrist position

        // Reset bar encoder if bar position is below threshold
        if (bar.getCurrentPosition() < -10) {
            bar.setPower(0);
            bar.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            bar.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        timir(1);                 // Wait 1 second

        Intake(1);                // Run intake motors outward (eject)
        timir(0.75);              // Wait 0.75 seconds

        Intake(0);                // Stop intake motors
        Barmove(100);             // Move bar motor to position 100
        timir(0.5);               // Wait 0.5 seconds

        LWrist.setPosition(0.75); // Move left wrist back to intake position
        timir(0.7);               // Wait 0.7 seconds

        Wrist.setPosition(0.2);   // Move wrist to intake position
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
     * Set power to intake motors.
     * intakeFront runs opposite direction to intakeBack.
     * @param power power level (-1 to 1)
     */
    private void Intake(double power) {
        intakeFront.setPower(power * -1);
        intakeBack.setPower(power);
    }

    /**
     * Main autonomous routine.
     * Initializes hardware and mechanisms, waits for start,
     * then performs a sequence of scoring and intake cycles.
     */
    @Override
    public void runOpMode() {
        initializeHardware();    // Initialize all hardware devices
        initializeMechanisms();  // Set mechanisms to starting positions

        waitForStart();         // Wait for start command

        if (opModeIsActive()) {
            Score();            // Perform scoring routine
            IntakeMacro();      // Perform intake routine
            Cords(-0.2, 1);    // Move to intake coordinates

            Score();            // Repeat scoring and intake cycles
            IntakeMacro();
            Cords(-0.2, 1);

            Score();
            IntakeMacro();
            Cords(-0.2, 1);

            Score();
        }
    }
}