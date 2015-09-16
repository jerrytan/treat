package com.gprinter.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.HRI_POSITION;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.GpCom;
import com.gprinter.command.TscCommand;
import com.gprinter.command.TscCommand.BARCODETYPE;
import com.gprinter.command.TscCommand.BITMAP_MODE;
import com.gprinter.command.TscCommand.DIRECTION;
import com.gprinter.command.TscCommand.EEC;
import com.gprinter.command.TscCommand.FONTMUL;
import com.gprinter.command.TscCommand.FONTTYPE;
import com.gprinter.command.TscCommand.MIRROR;
import com.gprinter.command.TscCommand.READABEL;
import com.gprinter.command.TscCommand.ROTATION;
import com.gprinter.io.GpDevice;
import com.tan.dnatreatment.R;
import com.tan.dnatreatment.dao.MyApplication;
import com.tan.dnatreatment.dao.TreatmentBarcode;
import com.tan.dnatreatment.dao.TreatmentInfo;
import com.tan.dnatreatment.util.APIConfig;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class PrintBarcodeActivity extends Activity {

	private GpService mGpService= null;
	public static final String CONNECT_STATUS = "connect.status";
	private static final String DEBUG_TAG = "PrintBarcodeActivity";
	public static final int GET_BARCODE =1;

	private PrinterServiceConnection conn = null;
    private  int mPrinterIndex = 0;

    private MyApplication mApplication ;
    private TreatmentInfo mTreatmentInfo ;
	private TreatmentBarcode mBarcode;
    private Button btnPrint;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GET_BARCODE:
					TextView txDisplayInfo = (TextView)findViewById(R.id.barcode_info);
					txDisplayInfo.setText(getDisplayString());
					break;
				default:
					break;
			}
		}
	};

	class PrinterServiceConnection implements ServiceConnection {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("ServiceConnection", "onServiceDisconnected() called");
			mGpService = null;
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("ServiceConnection", "onServiceConnected() called");
            mGpService =GpService.Stub.asInterface(service);
		} 
	}

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(DEBUG_TAG, "onResume");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_barcode);
		Log.e(DEBUG_TAG, "onCreate");

        mApplication = (MyApplication)getApplication();
        mTreatmentInfo = mApplication.getTreatmentInfo();

		getBarcode(mTreatmentInfo.getId());
		connection();

        btnPrint = (Button)findViewById(R.id.btPrint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerClicked();
            }
        });
        btnPrint.requestFocus();

	}

	private void getBarcode(final String id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(APIConfig.GET_BARCODE_URL);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					DataOutputStream out = new DataOutputStream(connection.getOutputStream());
					out.writeBytes("tid=" + id);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					String result = response.toString();

					JSONObject jsonObject = new JSONObject(result);
					String requestStatus = jsonObject.getString("Status");
					JSONObject jsonBarcode = jsonObject.getJSONObject("barcode");

					mBarcode = new TreatmentBarcode(
							id,jsonBarcode.getString("Customer"),
							jsonBarcode.getString("Step1"),jsonBarcode.getString("Step2"),jsonBarcode.getString("Step3"),
							jsonBarcode.getString("Step4"),jsonBarcode.getString("Step5"),jsonBarcode.getString("Step6"),
							jsonBarcode.getString("Blood"),
							jsonBarcode.getString("Drug1"),jsonBarcode.getString("Drug2"),jsonBarcode.getString("Drug3"),
							jsonBarcode.getString("Drug4"),jsonBarcode.getString("Drug5"),jsonBarcode.getString("Drug6")
							);
					mApplication.setBarcode(mBarcode);

					Log.d("Get Barcode ", "status is " + requestStatus);

					Message message = new Message();
					message.what = GET_BARCODE;
					handler.sendMessage(message);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	private void connection() {
		conn = new PrinterServiceConnection();
		Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
		bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	}

	public boolean[] getConnectState() {
		boolean[] state = new boolean[APIConfig.MAX_PRINTER_CNT];
		for (int i = 0; i < APIConfig.MAX_PRINTER_CNT; i++) {
			state[i] = false;
		}
		for (int i = 0; i < APIConfig.MAX_PRINTER_CNT; i++) {
				try {
					if (mGpService .getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
						state[i] = true;
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return state;
	}

	public void openPortDialogueClicked(View view) {
				Log.d(DEBUG_TAG, "openPortConfigurationDialog ");
				Intent intent = new Intent(this,
						PrinterConnectDialog.class);
				boolean[] state = getConnectState();
				intent.putExtra(CONNECT_STATUS, state);	
				this.startActivity(intent);
	}
	public void printTestPageClicked(View view) {
		try {
			int rel = mGpService.printeTestPage(mPrinterIndex); //
			Log.i("ServiceConnection", "rel " + rel);
			GpCom.ERROR_CODE r=GpCom.ERROR_CODE.values()[rel];
			if(r != GpCom.ERROR_CODE.SUCCESS){
				Toast.makeText(getApplicationContext(),GpCom.getErrorText(r),
						Toast.LENGTH_SHORT).show();	
		    }
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}



    public void printBarcodeInTSCMode(){

        TscCommand tsc = new TscCommand();
        tsc.addSize(40, 30);    //设置标签尺寸，按照实际尺寸设置
        tsc.addGap(2);           //设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addReference(0, 0);//设置原点坐标
        tsc.addUserCommand("SET TEAR ON\r\n");
        tsc.addCls();// 清除打印缓冲区


//        Bitmap b = BitmapFactory.decodeResource(getResources(),
//                R.drawable.appicon);
//        tsc.addBitmap(20, 20, BITMAP_MODE.OVERWRITE, b.getWidth(), b);
        //绘制简体中文
        tsc.addText(20, 50, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1, "以下为客户保存");

        tsc.addText(20, 70, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1, gereratePrinteString());

        tsc.add1DBarcode(20, 250, BARCODETYPE.EAN13, 100, READABEL.EANBEL, ROTATION.ROTATION_0, getCustomerBarCode());

        tsc.add1DBarcode(20, 250, BARCODETYPE.EAN13, 100, READABEL.EANBEL, ROTATION.ROTATION_0, getDrug1Barcode());

        tsc.addPrint(1, 1); // 打印标签

        Vector<Byte> datas = tsc.getCommand(); //发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendTscCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r=GpCom.ERROR_CODE.values()[rel];
            if(r != GpCom.ERROR_CODE.SUCCESS){
                Toast.makeText(getApplicationContext(),GpCom.getErrorText(r),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	public void printBarcodeInESCMode(){

		EscCommand esc = new EscCommand();
		esc.addPrintAndFeedLines((byte) 3);
		esc.addSelectJustification(JUSTIFICATION.CENTER);//设置打印居中
		esc.addText("以下为客户保存\n");   //  打印文字
		esc.addPrintAndLineFeed();

		/*打印文字*/
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);//取消倍高倍宽

        esc.addText(gereratePrinteString());   //  打印文字
        esc.addPrintAndFeedPaper((byte) 60);

        esc.addText("客户条码\n");
        esc.addSelectPrintingPositionForHRICharacters(HRI_POSITION.BELOW);//设置条码可识别字符位置在条码下方
		esc.addSetBarcodeHeight((byte) 60); //设置条码高度为60点
		esc.addEAN13(getCustomerBarCode());
        esc.addPrintAndFeedPaper((byte) 100);


        esc.addText("第一次治疗条码\n");
        esc.addEAN13(getStep1Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第二次治疗条码\n");
        esc.addEAN13(getStep2Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第三次治疗条码\n");
        esc.addEAN13(getStep3Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第四次治疗条码\n");
        esc.addEAN13(getStep4Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第五次治疗条码\n");
        esc.addEAN13(getStep5Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第六次治疗条码\n");
        esc.addEAN13(getStep6Barcode());
        esc.addPrintAndFeedPaper((byte) 50);

        esc.addText("以下为员工保存\n");
        esc.addPrintAndLineFeed();
        esc.addPrintAndFeedPaper((byte) 50);

        esc.addText("血样条码\n");
        esc.addEAN13(getBloodBarcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第一次药的条码\n");
        esc.addEAN13(getDrug1Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第二次药的条码\n");
        esc.addEAN13(getDrug2Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第三次药的条码\n");
        esc.addEAN13(getDrug3Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第四次药的条码\n");
        esc.addEAN13(getDrug4Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第五次药的条码\n");
        esc.addEAN13(getDrug5Barcode());
        esc.addPrintAndFeedPaper((byte) 100);

        esc.addText("第六次药的条码\n");
        esc.addEAN13(getDrug6Barcode());
        esc.addPrintAndFeedPaper((byte) 100);
		
		Vector<Byte> datas = esc.getCommand(); //发送数据
		Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
		byte[] bytes = ArrayUtils.toPrimitive(Bytes);
		String str = Base64.encodeToString(bytes, Base64.DEFAULT);
		int rel;
		try {
			rel = mGpService.sendEscCommand(mPrinterIndex, str);
			GpCom.ERROR_CODE r=GpCom.ERROR_CODE.values()[rel];
			if(r != GpCom.ERROR_CODE.SUCCESS){
				Toast.makeText(getApplicationContext(),GpCom.getErrorText(r),
						Toast.LENGTH_SHORT).show();	
		          }			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendLabel(){
		TscCommand tsc = new TscCommand();
		tsc.addSize(60, 60); //设置标签尺寸，按照实际尺寸设置
		tsc.addGap(2);           //设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
    	tsc.addDirection(DIRECTION.BACKWARD,MIRROR.NORMAL);//设置打印方向
    	tsc.addReference(0, 0);//设置原点坐标
    // 	tsc.addTear(ENABLE.ON); //撕纸模式开启
     	tsc.addUserCommand("SET TEAR ON\r\n");
    	tsc.addCls();// 清除打印缓冲区
    	//绘制简体中文
     	tsc.addText(20,20,FONTTYPE.SIMPLIFIED_CHINESE,ROTATION.ROTATION_0,FONTMUL.MUL_1,FONTMUL.MUL_1,"Welcome to use Gprinter!");
     	//绘制图片
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.gprinter);
		tsc.addBitmap(20,50, BITMAP_MODE.OVERWRITE, b.getWidth(),b);
		
		tsc.addQRCode(250, 80, EEC.LEVEL_L,5,ROTATION.ROTATION_0, " www.gprinter.com.cn");	
     	//绘制一维条码
     	tsc.add1DBarcode(20,250, BARCODETYPE.CODE128, 100, READABEL.EANBEL, ROTATION.ROTATION_0, "Gprinter");
    	tsc.addPrint(1,1); // 打印标签
    	tsc.addSound(2, 100); //打印标签后 蜂鸣器响
		Vector<Byte> datas = tsc.getCommand(); //发送数据
		Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
		byte[] bytes = ArrayUtils.toPrimitive(Bytes);
		String str = Base64.encodeToString(bytes, Base64.DEFAULT);
		int rel;
		try {
			rel = mGpService.sendTscCommand(mPrinterIndex, str);
			GpCom.ERROR_CODE r=GpCom.ERROR_CODE.values()[rel];
			if(r != GpCom.ERROR_CODE.SUCCESS){
				Toast.makeText(getApplicationContext(),GpCom.getErrorText(r),
						Toast.LENGTH_SHORT).show();	
		          }			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	


	public void printerClicked() {
		try {
			int type = mGpService.getPrinterCommandType(mPrinterIndex);
            int status = mGpService.queryPrinterStatus(mPrinterIndex,500);
            if (status == GpCom.STATE_NO_ERR) {
                printBarcodeInESCMode();

//                if (type == GpCom.ESC_COMMAND) {
//                    printBarcodeInESCMode();
//                } else if (type == GpCom.TSC_COMMAND) {
//                    printBarcodeInTSCMode();
//                }
            }
            else{
                Toast.makeText(getApplicationContext(),"打印机错误！", Toast.LENGTH_SHORT).show();
            }
		}
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	


	@Override
	public void onDestroy() {
		Log.e(DEBUG_TAG, "onDestroy");
		super.onDestroy();
		if (conn != null) {
			unbindService(conn); // unBindService
		}
	}
    // TODO: 2015/8/27
    //will be moved to util lib later
    private String gereratePrinteString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("客户姓名：");buffer.append(mTreatmentInfo.getCustomerName());buffer.append("\n");
        buffer.append("客户电话：");buffer.append(mTreatmentInfo.getCustomerPhone());buffer.append("\n");
        buffer.append("开始时间：");buffer.append(mTreatmentInfo.getStartDate());buffer.append("\n");
        buffer.append("结束时间：");buffer.append(mTreatmentInfo.getEndDate());buffer.append("\n");
        return buffer.toString();
    }
    private String getCustomerBarCode() {
        return mBarcode.getCustomerBarcode();
    }
    private String getStep1Barcode() {
        return mBarcode.getStep1Barcode();
    }
	private String getStep2Barcode() {
		return mBarcode.getStep2Barcode();
	}
	private String getStep3Barcode() {
		return mBarcode.getStep3Barcode();
	}
    private String getStep4Barcode() {
        return mBarcode.getStep4Barcode();
    }
    private String getStep5Barcode() {
        return mBarcode.getStep5Barcode();
    }
    private String getStep6Barcode() {
        return mBarcode.getStep6Barcode();
    }
    private String getBloodBarcode() {
        return mBarcode.getBloodBarcode();
    }
    private String getDrug1Barcode() {
        return mBarcode.getDrug1Barcode();
    }
    private String getDrug2Barcode() {
        return mBarcode.getDrug2Barcode();
    }
    private String getDrug3Barcode() {
        return mBarcode.getDrug3Barcode();
    }
    private String getDrug4Barcode() {
        return mBarcode.getDrug4Barcode();
    }
    private String getDrug5Barcode() {
        return mBarcode.getDrug5Barcode();
    }
    private String getDrug6Barcode() {
        return mBarcode.getDrug6Barcode();
    }

	private String getDisplayString() {
        String res = gereratePrinteString()+"客户条码\n"+ getCustomerBarCode()+"\n第一次治疗条码\n"+
                getStep1Barcode()+"\n第二次治疗条码\n"+ getStep2Barcode()+"\n第三次治疗条码\n"+
                getStep3Barcode()+ "\n第四次治疗条码\n" +getStep4Barcode()+"\n第五次治疗条码\n"+
                getStep5Barcode()+"\n第六次治疗条码\n" + getStep6Barcode();
        return res;
    }

}
