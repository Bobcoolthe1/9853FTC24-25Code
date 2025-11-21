
package org.firstinspires.ftc.teamcode;
//put all of the imports here
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Autonomous(name="Autojr", group="Linear OpMode")
//@Disabled
public class Autojr2425 extends LinearOpMode {

    //private hardware map
    private DcMotor backRight = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor frontLeft = null;
    private DcMotor SlideLeft = null;
    private DcMotor SlideRight = null;
    private  CRServo intakeFront = null;
    private  CRServo intakeBack = null;
    private  Servo Bucket = null;
    private  Servo Wrist = null;
    private  DcMotor bar = null;
    private  Servo ITR = null;
    private TouchSensor LeftSensor = null;
    private TouchSensor RightSensor = null;

    @Override
    public void runOpMode() throws InterruptedException {
        initializeHardware();
        initializeMechanisms();
        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        if (opModeIsActive()) {
            for (int i = 0; i < 3; i++) {
                telemetry.addData("Status", "Approaching block " + (i + 1));
                telemetry.update();

                initializeMechanisms(); //Initialize the mechanisms again to reset any previous positions

                // Move forward to approach the block
                Forward(1, 1000);  // Adjust power and duration as needed

                //Lower the intake mechanism
                Barmove(600, 1.0);
                Intake(1);
                sleep(1000);  // Run intake for 1 second

                // Brings the bar up
                Barmove(0, 1.0);
                Wrist.setPosition(0);
                ITR.setPosition(0);
                sleep(1000);  // Wait for mechanisms to move

                // Put the block in the basket
                Intake(-1);
                sleep(500);  // Run intake for 0.5 seconds

                // Stop intake and rest the bar
                Intake(0);
                Barmove(120, 1.0);

                // Move backwards slightly
                Backward(1, 500);  // Adjust as needed

                // Turn towards the backdrop
                SpinClockwise(0.5, 250);  // Adjust rotation as needed

                // Move towards the backdrop
                Backward(1, 1000);  // Adjust distance as needed

                // Raise slides to scoring position
                Slidesmove(5150, 1.0);
                sleep(1000);  // Wait for slides to extend

                // Score the block
                Bucket.setPosition(0.4);
                sleep(1000);
                Bucket.setPosition(0.8);

                // Lower slides
                Slidesmove(-200, 0.75);
                if (LeftSensor.isPressed() || RightSensor.isPressed()) {
                    // Reset SlideLeft encoder to 0
                    SlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    SlideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    SlideLeft.setPower(0);
                    SlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    SlideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    SlideRight.setPower(0);
                    Bucket.setPosition(1);
                }

                //Move forward to position for next block
                Forward(1, 2000);  // Adjust distance as needed
                // Turn back to face the next block
                SpinCounterClockwise(0.5, 250);  // Adjust rotation as needed

                Forward(1, 500);  // Adjust distance as needed

                // Move to position for next block
                if (i < 2) {  // Don't do this for the last block
                    Right(0.5, 500);  // Adjust distance to move to the next block
                }
            }

            // Add parking logic here
            parkRobot();
        }
    }

    private void initializeHardware() {
        backLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");

        bar = hardwareMap.get(DcMotor.class, "bar");
        bar.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bar.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bar.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeFront = hardwareMap.get(CRServo.class, "intakeFront");
        intakeBack = hardwareMap.get(CRServo.class, "intakeBack");

        Bucket = hardwareMap.get(Servo.class, "bucket");
        Wrist = hardwareMap.get(Servo.class, "wrist");
        ITR = hardwareMap.get(Servo.class, "wristRight");

        SlideLeft = hardwareMap.get(DcMotor.class, "leftbar");
        SlideRight = hardwareMap.get(DcMotor.class, "rightBar");
        SlideLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SlideRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SlideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SlideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RightSensor = hardwareMap.get(TouchSensor.class, "rightBarSensor");
        LeftSensor = hardwareMap.get(TouchSensor.class, "leftBarSensor");

        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        SlideLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        SlideRight.setDirection(DcMotorSimple.Direction.FORWARD);

        DcMotor[] driveMotors = {frontLeft, frontRight, backRight, backLeft};
        for (DcMotor motor : driveMotors) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    private void initializeMechanisms() {
        Bucket.setPosition(1);
        Barmove(120, 1.0);
        Slidesmove(0, 1.0);
    }

    private void parkRobot() {
        // Implement parking logic here
        Forward(1, 1000);  // Example: move forward to park
    }

    // Helper methods (Barmove, Slidesmove, Intake, movement methods) remain the same
    private void Barmove(int targetPosition, double power) {
        bar.setTargetPosition(targetPosition);
        bar.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bar.setPower(power);
    }

    private void Slidesmove(int targetPosition, double power) {
        SlideLeft.setTargetPosition(targetPosition);
        SlideRight.setTargetPosition(targetPosition);
        SlideLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        SlideRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        SlideLeft.setPower(power);
        SlideRight.setPower(power);
    }

    private void Intake(double power) {
        intakeFront.setPower(power);
        intakeBack.setPower(power * -1);
    }

    private void Forward(double power, int duration) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backRight.setPower(power);
        backLeft.setPower(power);
        sleep(duration);
    }

    private void Backward(double power, int duration) {
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backRight.setPower(-power);
        backLeft.setPower(-power);
        sleep(duration);
    }

    private void Left(double power, int duration) {
        frontLeft.setPower(power * -1);
        frontRight.setPower(power);
        backRight.setPower(power * -1);
        backLeft.setPower(power);
        sleep(duration);
    }

    private void Right(double power, int duration) {
        frontLeft.setPower(power);
        frontRight.setPower(power * -1);
        backRight.setPower(power);
        backLeft.setPower(power * -1);
        sleep(duration);
    }

    private void SpinClockwise(double power, int duration) {
        frontLeft.setPower(power);
        frontRight.setPower(-power);
        backRight.setPower(-power);
        backLeft.setPower(power);
        sleep(duration);
    }

    private void SpinCounterClockwise(double power, int duration) {
        frontLeft.setPower(-power);
        frontRight.setPower(power);
        backRight.setPower(power);
        backLeft.setPower(-power);
        sleep(duration);
    }
}
