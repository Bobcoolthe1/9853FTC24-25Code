package org.firstinspires.ftc.teamcode;
//put all of the imports here
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;




@TeleOp(name="Telejr2425", group="Linear OpMode")
//@Disabled
public class Telejr2425 extends LinearOpMode {




    // drive motors
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
    private void timir(time) {
        ElapsedTime timer3 = new ElapsedTime();
        while (opModeIsActive() && timer3.seconds() < time) {
            telemetry.addData("timir-tRuEeEeEeEe");
            telemetry.update();
        }
    }


    @Override
    public void runOpMode() throws InterruptedException {


        backLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");


        // Declare:
        //other motors
        bar = hardwareMap.get(DcMotor.class, "bar");
        bar.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bar.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bar.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //CRServos
        intakeFront = hardwareMap.get(CRServo.class, "intakeFront");
        intakeBack = hardwareMap.get(CRServo.class, "intakeBack");
        //Servos
        Bucket = hardwareMap.get(Servo.class, "bucket");
        Bucket.setPosition(1);
        Wrist = hardwareMap.get(Servo.class, "wrist");
        ITR = hardwareMap.get(Servo.class, "wristRight");
        //Slides
        SlideLeft = hardwareMap.get(DcMotor.class, "leftbar");
        SlideLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SlideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SlideRight = hardwareMap.get(DcMotor.class, "rightBar");
        SlideRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        SlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SlideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Sensors
        RightSensor = hardwareMap.get(TouchSensor.class, "rightBarSensor");
        LeftSensor = hardwareMap.get(TouchSensor.class, "leftBarSensor");


        //Direction
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


        // telemetry.addData("SlideLeft-Pos", SlideLeft.getCurrentPosition());
        //telemetry.addData("SlideLeft-Pow", SlideLeft.getPower());
        //telemetry.addData("SlideRight-Pos", SlideRight.getCurrentPosition());
        //telemetry.addData("SlideRight-Pow", SlideRight.getPower());
        telemetry.addData("bar-Pos", bar.getCurrentPosition());
        telemetry.addData("ITR-Pos", ITR.getPosition());
        // telemetry.addData("Bucket-Pos",Bucket.getPosition());
        telemetry.addData("Wrist-Pos", Wrist.getPosition());
        telemetry.addData("IntakeFront-Pow", intakeFront.getPower());
        telemetry.addData("IntakeBack-Pow", intakeBack.getPower());
        //telemetry.addData("frontLeft-Pow", frontLeft.getPower());
        //telemetry.addData("frontRight-Pow", frontRight.getPower());
        //telemetry.addData("backLeft-Pow", backLeft.getPower());
        //telemetry.addData("backRight-Pow", backRight.getPower());
        telemetry.update();


        // Wait for the game to start (driver presses START)
        waitForStart();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {




            if (gamepad2.left_bumper) {
                bar.setPower(0.7);
            } else if (gamepad2.right_bumper) {
                bar.setPower(-0.7);
            } else {
                bar.setPower(0);
            }


            //inputs for drive
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x * 1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            // Math to get outputs of how much power each motor gets
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = ((y + x + rx) / denominator);
            double backLeftPower = ((y - x + rx) / denominator);
            double frontRightPower = ((y - x - rx) / denominator);
            double backRightPower = ((y + x - rx) / denominator);
            //Brake code
            if (frontLeftPower == 0) {
                frontLeft.setPower(0);
            }
            if (backLeftPower == 0) {
                backLeft.setPower(0);
            }
            if (frontRightPower == 0) {
                frontRight.setPower(0);
            }
            if (backRightPower == 0) {
                backRight.setPower(0);
            }
            //setting power for motors
            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);


            // Intake
            if (gamepad2.left_trigger > 0) {
                Intake(1);
            } else if (gamepad2.right_trigger > 0) {
                Intake(-1);
            } else {
                Intake(0);
            }


            boolean GO1 = true;
            if (gamepad2.dpad_up) {
                ITR.setPosition(0.7);
            }
            if (gamepad2.dpad_down) {
                ITR.setPosition(0);
            }
            if (gamepad2.dpad_left) {
                Wrist.setPosition(0.7);
            }
            if (gamepad2.dpad_right) {
                Wrist.setPosition(0);
            }
            if (ITR.getPosition() == 0) {
                GO1 = false;
            }


            //intake Marco
            if(bar.getCurrentPosition() < -10){
                bar.setPower(0);
                bar.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                bar.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            if (gamepad2.y) {
                Barmove(-15, 1.0);
                Wrist.setPosition(0);
                ITR.setPosition(0);
                Intake(0);


//Resets the bar's Position
                if (bar.getPosition() < -10){
                    bar.setPower(0);
                    bar.STOP_AND_RESET_ENCODERS
                    bar.START_USING_ENCODERS
                }


//Gives time to do that
                timir(3);


//Spits out block
                Intake(-1);
                timir(1.5);


//Stopped
                Intake(0);
                barmove(140);


            }


            //Slide Macro
            if (gamepad2.x) {
                GO1 = true;
                Slidesmove(5150, 1.0);
            }
            if (SlideLeft.getCurrentPosition() > 5140 && SlideLeft.getCurrentPosition() < 5160) {
                Bucket.setPosition(0.4);
                sleep(1000);
            }
            if(Bucket.getPosition() > 0.37 && Bucket.getPosition() < 0.415){
                GO1 = false;l
                Bucket.setPosition(0.8);
                Slidesmove(-200,0.75);
            }




            // Touch Sensors
            if (LeftSensor.isPressed() || RightSensor.isPressed() && GO1 == false) {
                // Reset SlideLeft encoder to 0
                SlideLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                SlideLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                SlideLeft.setPower(0);
                SlideRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                SlideRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                SlideRight.setPower(0);
                Bucket.setPosition(1);
            }
            if(){




            }




            // telemetry.addData("SlideLeft-Pos", SlideLeft.getCurrentPosition());
            //telemetry.addData("SlideLeft-Pow", SlideLeft.getPower());
            //telemetry.addData("SlideRight-Pos", SlideRight.getCurrentPosition());
            //telemetry.addData("SlideRight-Pow", SlideRight.getPower());
            telemetry.addData("bar-Pos", bar.getCurrentPosition());
            telemetry.addData("ITR-Pos", ITR.getPosition());
            // telemetry.addData("Bucket-Pos",Bucket.getPosition());
            telemetry.addData("Wrist-Pos", Wrist.getPosition());
            telemetry.addData("IntakeFront-Pow", intakeFront.getPower());
            telemetry.addData("IntakeBack-Pow", intakeBack.getPower());
            //telemetry.addData("frontLeft-Pow", frontLeft.getPower());
            //telemetry.addData("frontRight-Pow", frontRight.getPower());
            //telemetry.addData("backLeft-Pow", backLeft.getPower());
            //telemetry.addData("backRight-Pow", backRight.getPower());
            telemetry.update();
        }
    }
}







