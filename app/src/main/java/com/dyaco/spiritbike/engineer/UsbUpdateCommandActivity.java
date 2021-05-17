package com.dyaco.spiritbike.engineer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.corestar.libs.ota.McuTerminator;
import com.corestar.libs.ota.McuUpdateManager;
import com.corestar.libs.utils.StringUtil;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.RxTimer;
import com.samade.libusb.UsbReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class UsbUpdateCommandActivity extends AppCompatActivity {

	private static final String TAG = UsbUpdateCommandActivity.class.getSimpleName();
	private List<UsbReader.CSUsbDevice> m_CSUsbDevice = new ArrayList<>();
	private Handler m_Handler;
	private Handler m_HandlerTimeOut;
	private Button m_BtnCommandClose;
	private Button m_BtnCommandOTA;
	private Context m_Context;
	private UsbReader m_UsbReader;
	private McuUpdateManager m_McuUpdateManager;
	private TextView m_TvLog;
	private TextView m_TextViewSchedule;
	private SpannableStringBuilder m_SpannableStringBuilder;
	private ProgressBar m_ProgressBarSchedule;
	private AlertDialog.Builder m_Builder;
	private AlertDialog m_AlertDialog;
	private Boolean m_UpdateState = false;
	private byte[] m_Data;
	private byte[][] m_OTA_Data;
	private boolean m_Run = true;
	private int m_SendCommandNumber = 0;
	private int m_Schedule;
	private int m_Time = 0;
	private int m_Size;
	private long m_RunnableTime = 60;
	private String m_BIN_Name;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usb_update_command);


		getInstance().mDevice.disconnect();

//        initData();
		initViews();
		initManager();


		m_Context = this;

		if (Build.VERSION.SDK_INT >= 23) {
			int REQUEST_CODE_PERMISSION_STORAGE = 100;
			String[] permissions = {
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE
			};

			for (String str : permissions) {
				if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
					this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
					return;
				}
			}
		}

		m_BtnCommandClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_Run == true) {
					m_Handler.removeCallbacksAndMessages(null);
					m_HandlerTimeOut.removeCallbacksAndMessages(null);
					Intent intent = new Intent();
					intent.putExtra("updateState", m_UpdateState);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					buttonAbort();
				}
			}
		});

		m_BtnCommandOTA.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_Run == true) {

					m_UsbReader.unregisterListener();
					m_McuUpdateManager.disconnect();
					initManager();

					m_Time = 0;
					m_Handler = new Handler();
					m_HandlerTimeOut = new Handler();
					log(true);
					m_Run = false;
					m_BtnCommandOTA.setVisibility(View.INVISIBLE);
					btnFindDevice();
				}

			}
		});
	}


	//20210208_Martin_++
	/*
	outputResult 與 getData
	兩個方法為讀取到USB檔案的Data值後整理成一個二維陣列
	並加入CS1與CS2值
	 */
	private void outputResult(byte[] data) {
		m_Size = (int) Math.ceil(((double) data.length / (double) 32));
		m_OTA_Data = new byte[m_Size + 1][34];
		m_OTA_Data[m_Size] = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x20, (byte) 0xBA, 0x0A};
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			if (i % 32 == 0) {
				m_OTA_Data[j] = getData(data, i);
				j++;
			}
		}
	}

	public static byte[] getData(byte[] data, int number) {
		int dataMax = 0;
		int outputnum = 0;
		int lastLong = 0;
		byte cs1Sum = 0;
		byte cs2Sum = 0;
		byte inner_sum = 0;
		byte[] outputdata = new byte[34];

		dataMax = number + 31;

		for (int i = number; i <= dataMax; i++) {
			if (i >= data.length) {
				lastLong++;
				if (outputnum == 31) {
					outputdata[31] = (byte) (32 - lastLong);
				} else {
					outputdata[outputnum] = 0;
				}
			} else {
				outputdata[outputnum] = data[i];
			}
			outputnum++;
		}

		for (int i = 0; i < 32; i++) {
			cs1Sum += outputdata[i];
			inner_sum = 0;
			for (int j = 0; j <= i; j++) {
				inner_sum += outputdata[j];
			}
			cs2Sum += inner_sum;

		}

		if (dataMax > data.length) {
			outputdata[32] = (byte) (cs1Sum + 0x55);
			outputdata[33] = (byte) (cs2Sum + 0x55);
		} else {
			outputdata[32] = cs1Sum;
			outputdata[33] = cs2Sum;
		}
		return outputdata;
	}

	private void initData() {
		int i = 0;
		for (byte b : m_Data) {
			m_Data[i] = (byte) (i + 1);
			i++;
		}
	}

	private void initManager() {

		m_UsbReader = new UsbReader(this);
		m_UsbReader.setListener(new UsbReader.UsbReaderListener() {
			@Override
			public void onRequestPermission(UsbReader.CSUsbDevice csUsbDevice) {
				String log = "on request permission, device: " + csUsbDevice.getName() + ", granted: " + csUsbDevice.isPermissionGranted();
				log(log);
				if (csUsbDevice.isPermissionGranted() == true) {
					new RxTimer().timer(1000, n -> {
						btnFindJsonFile();
					});
				} else {
					m_Run = true;
					m_Time = 0;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							m_BtnCommandOTA.setVisibility(View.VISIBLE);
						}
					});
					return;
				}
			}

			/*
			取得USB檔案的Data值
			 */
			@Override
			public void onFindFile(String file, UsbReader.FILE_STATUS status, UsbReader.FILE_TYPE type, String data, byte[] raw) {
				String log = "on find file, file name: " + file + "\n" + ", status: " + status + "\n" + ", type: " + type + ", raw: " + (raw != null ? raw.length : null);
				log(log);


				switch (file.toString()){
					case "update.json":
						try {
							JSONObject updataJson = new JSONObject(data);
							m_BIN_Name = String.valueOf(updataJson.get("PATH"));
							btnFindBinFile(m_BIN_Name);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						break;
					default:
						if (raw == null) {
							m_Run = true;
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									m_BtnCommandOTA.setVisibility(View.VISIBLE);
								}
							});
							return;
						}
						m_Size = raw.length;
						outputResult(raw);
						btnSendCommand();
						break;
				}
			}

			@Override
			public void onDeviceAttached(String name) {
				String log = "on device attached, device name: " + name;
				log(log);

			}

			@Override
			public void onDeviceDetached(String name) {
				String log = "on device detached, device name: " + name;
				log(true);
				log(log);
				clear();
				m_Time = 0;
				m_HandlerTimeOut.removeCallbacks(m_BoxTimeOut);
				m_BtnCommandOTA.setVisibility(View.VISIBLE);
				m_Handler.removeCallbacksAndMessages(null);
				m_HandlerTimeOut.removeCallbacksAndMessages(null);
			}
		});

		m_McuUpdateManager = new McuTerminator();
		m_McuUpdateManager.setUpdateListener(new McuUpdateManager.UpdateListener() {
			@Override
			public void onConnectFail() {
				Log.e(TAG, "on connect fail.");
			}

			@Override
			public void onConnected() {
				Log.d(TAG, "on connected.");
			}

			@Override
			public void onDisconnected() {
				Log.d(TAG, "on disconnected.");
			}

			@Override
			public void onDataSent(byte[] data) {
				Log.d(TAG, "on data sent, data: " + StringUtil.bytesToHex(data, true));
			}

			/*
			判斷erase是否有執行成功
			 */
			@Override
			public void onEraseState(McuUpdateManager.STATE state) {
				Log.d(TAG, "on erase state: " + state);
				if (state == McuUpdateManager.STATE.OK) {
					log("on erase state: " + state);

					m_Handler.post(m_Runnable);

				} else {
					log("on erase state: " + state);
					m_Run = true;
					m_Time = 0;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							m_BtnCommandOTA.setVisibility(View.VISIBLE);
						}
					});
					m_Handler.removeCallbacksAndMessages(null);
					m_HandlerTimeOut.removeCallbacksAndMessages(null);
					return;
				}
			}

			/*
			封包遺失由此通知
			 */
			@Override
			public void onResendRequest(int index) {
				Log.d(TAG, "on resend request, index: " + index);
				stop();
				m_McuUpdateManager.write(index, m_OTA_Data[index]);
				m_Handler.postDelayed(m_Runnable, m_RunnableTime);

			}

			/*
			更新成功或失敗由此通知
			 */
			@Override
			public void onUpdateState(McuUpdateManager.STATE state) {
				Log.d(TAG, "on update state: " + state);
				m_HandlerTimeOut.removeCallbacks(m_TimeOut);
				if (state == McuUpdateManager.STATE.OK) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							log("on update state: " + state);
							m_UpdateState = true;
							clear();
							m_Time = 0;
						}
					});
				} else {
					log("on update state: " + state);
					m_UpdateState = false;
					clear();
					m_Time = 0;
					m_HandlerTimeOut.removeCallbacks(m_TimeOut);
				}
				m_Handler.removeCallbacksAndMessages(null);
				m_HandlerTimeOut.removeCallbacksAndMessages(null);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						m_BtnCommandOTA.setVisibility(View.VISIBLE);
					}
				});
			}
		});

		m_McuUpdateManager.connect();
	}

	@SuppressLint("SetTextI18n")
	private void initViews() {

		m_Handler = new Handler();
		m_HandlerTimeOut = new Handler();

		m_SpannableStringBuilder = new SpannableStringBuilder();
		m_BtnCommandClose = (Button) findViewById(R.id.bt_command_close);
		m_BtnCommandOTA = (Button) findViewById(R.id.btn_command_osOTA);
		m_ProgressBarSchedule = findViewById(R.id.progressBar_schedule);
		m_TextViewSchedule = findViewById(R.id.textView_schedule);
		m_TvLog = findViewById(R.id.tv_command_version);

		m_TextViewSchedule.setText(0 + " % ");
		m_ProgressBarSchedule.setProgress(0);

		m_TvLog.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				m_SpannableStringBuilder.clear();
				m_TvLog.setText("");
				return true;
			}
		});


	}

	private void log(String message) {
		Log.d(TAG, message);

		SpannableString spannableString = new SpannableString(message + "\n\n");
		m_SpannableStringBuilder.append(spannableString);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				m_TvLog.setText(m_SpannableStringBuilder, TextView.BufferType.SPANNABLE);

			}
		});
	}

	private void log(Boolean clear) {

		m_SpannableStringBuilder.clear();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				m_TvLog.setText("");

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		m_Handler.removeCallbacksAndMessages(null);
		m_HandlerTimeOut.removeCallbacksAndMessages(null);
		if (m_UsbReader != null) {
			m_UsbReader.unregisterListener();
		}
		if (m_McuUpdateManager != null) {
			m_McuUpdateManager.disconnect();
		}
	}


	/*
	控制送去Command的數值與速度
	 */
	private Runnable m_Runnable = new Runnable() {
		@SuppressLint("SetTextI18n")
		@Override
		public void run() {

//            if (m_SendCommandNumber == m_OTA_Data.length) {
//                stop();
//                clear();
//            } else
			if (m_Size == m_SendCommandNumber) {
				stop();
				m_Builder = new AlertDialog.Builder(m_Context);
				m_Builder.setTitle("選擇動作");
				m_Builder.setMessage("是否更新");
				m_Builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						m_McuUpdateManager.write(m_SendCommandNumber, m_OTA_Data[m_SendCommandNumber]);
						m_TextViewSchedule.setText(100 + " % ");
						m_ProgressBarSchedule.setProgress(100);
						m_SendCommandNumber = 0;
						m_Run = true;
						m_Time = 0;
						m_HandlerTimeOut.removeCallbacks(m_BoxTimeOut);
						m_HandlerTimeOut.post(m_TimeOut);
					}
				});
				m_Builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						clear();
						m_Time = 0;
						log(true);
						log("Abort");
						m_BtnCommandOTA.setVisibility(View.VISIBLE);
						m_HandlerTimeOut.removeCallbacks(m_BoxTimeOut);
						m_Handler.removeCallbacksAndMessages(null);
						m_HandlerTimeOut.removeCallbacksAndMessages(null);
					}
				});
				m_AlertDialog = m_Builder.create();
				m_AlertDialog.show();
			} else {
				m_McuUpdateManager.write(m_SendCommandNumber, m_OTA_Data[m_SendCommandNumber]);
				m_Schedule = (int) (((double) m_SendCommandNumber / (double) m_Size) * 100);
				m_TextViewSchedule.setText(m_Schedule + " % ");
				m_ProgressBarSchedule.setProgress(m_Schedule);
				m_SendCommandNumber++;
				m_Handler.postDelayed(m_Runnable, m_RunnableTime);

			}
		}
	};

	/*
		停止
		 */
	public void stop() {
		m_Handler.removeCallbacks(m_Runnable);
		m_HandlerTimeOut.post(m_BoxTimeOut);
	}

	/*
	清除
	 */
	public void clear() {
		m_TextViewSchedule.setText(0 + " % ");
		m_ProgressBarSchedule.setProgress(0);
		m_Schedule = 0;
		m_SendCommandNumber = 0;
		m_Run = true;
	}

	/*
	Box等待時間太長會中止視窗並清除資料
	 */
	private Runnable m_BoxTimeOut = new Runnable() {

		@Override
		public void run() {
			Log.d("@@@@", "m_BoxTimeOut = " + m_Time);
			if (m_Time == 30) {
				m_HandlerTimeOut.removeCallbacks(m_BoxTimeOut);
				m_AlertDialog.dismiss();
				clear();
				m_Time = 0;
				log(true);
				log("Wait Too Long Please Restart Send CMD");
				m_BtnCommandOTA.setVisibility(View.VISIBLE);
				m_Handler.removeCallbacksAndMessages(null);
				m_HandlerTimeOut.removeCallbacksAndMessages(null);
			} else {
				m_Time++;
				m_HandlerTimeOut.postDelayed(this, 1000);
			}
		}
	};

	/*
	下控無回應太久，會通知更新失敗並初始化
	 */
	private Runnable m_TimeOut = new Runnable() {
		@Override
		public void run() {
			Log.d("@@@@", "m_TimeOut = " + m_Time);
			if (m_Time == 30) {
				log("on update state: NG");
				m_Time = 0;
				clear();
				m_HandlerTimeOut.removeCallbacks(m_TimeOut);
				m_BtnCommandOTA.setVisibility(View.VISIBLE);
			} else {
				m_Time++;
				m_HandlerTimeOut.postDelayed(this, 1000);
			}
		}
	};


//    private Runnable m_FindBinFileReciprocal = new Runnable() {
//        @Override
//        public void run() {
//            Log.d("@@@@", "m_TimeOut = " + m_Time);
//            if (m_Time == 1) {
//                btnFindBinFile("Nuc029LDE_SubMCU_20210220B.BIN");
//                m_HandlerTimeOut.removeCallbacks(m_FindBinFileReciprocal);
//                m_Time = 0;
//            } else {
//                m_Time++;
//                m_HandlerTimeOut.postDelayed(this, 1000);
//            }
//        }
//    };


//    private Runnable m_SendCommandReciprocal = new Runnable() {
//        @Override
//        public void run() {
//            Log.d("@@@@", "m_TimeOut = " + m_Time);
//            if (m_Time == 1) {
//                btnSendCommand();
//                m_HandlerTimeOut.removeCallbacks(m_FindBinFileReciprocal);
//                m_Time = 0;
//            } else {
//                m_Time++;
//                m_HandlerTimeOut.postDelayed(this, 1000);
//            }
//        }
//    };

	/*
		列印陣列
	 */
	public void printData() {
		try {
			for (int i = 0; i < m_OTA_Data.length; i++) {
				Log.d("@@@@", i + "  =  " + StringUtil.bytesToHex(m_OTA_Data[i], true));
				Thread.sleep(30);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	確認是否有USB裝置
	*/
	private void btnFindDevice() {
		Log.d("$$$$$$$$$", "btnFindDevice");
		log("finding device...");

		m_CSUsbDevice = m_UsbReader.getUsbDevice();

		if (m_CSUsbDevice.size() > 0) {
			String log = "usb device size: " + m_CSUsbDevice.size();
			log(log);
			for (UsbReader.CSUsbDevice device : m_CSUsbDevice) {
				String name = "device name: " + device.getName() + ", permission: " + device.isPermissionGranted();
				log(name);
			}
			btnRequestPermission();
		} else {
			log("device not found.");
			m_Run = true;
			m_BtnCommandOTA.setVisibility(View.VISIBLE);
		}

	}

	/*
	取得讀取USB權限
	*/
	private void btnRequestPermission() {
		Log.d("$$$$$$$$$", "btnRequestPermission");
		log("requesting permission...");
		if (m_CSUsbDevice.size() > 0) {
			m_UsbReader.requestPermission(m_CSUsbDevice.get(0));
		}
	}

	/*
	讀取USB
	USB讀取Json
	*/
	private void btnFindJsonFile(){
		String file_name = "update.json";

		log("finding file... : " + file_name);

		if (m_CSUsbDevice.size() > 0) {
			m_UsbReader.findFile(m_CSUsbDevice.get(0), file_name, UsbReader.FILE_TYPE.JSON);
		}
	}

	/*
	讀取USB
	USB讀取BIN
	*/
	private void btnFindBinFile(String name) {
		Log.d("$$$$$$$$$", "btnFindBinFile");
		String file_name = name;
		log(true);
		log("find file... : " + file_name);
		if (m_CSUsbDevice.size() > 0) {
			m_UsbReader.findFile(m_CSUsbDevice.get(0), file_name, UsbReader.FILE_TYPE.BIN);
		}
	}

	/*
	啟動更新按鈕
	*/
	private void btnSendCommand() {
		Log.d("$$$$$$$$$", "btnSendCommand");

		// erase data
		//m_McuUpdateManager.erase();

		// write date
		//mcuUpdateManager.write(32, data);

//		if (m_Run == true) {
		m_McuUpdateManager.erase();
//		}
	}

	/*
	中止按鈕
	 */
	private void buttonAbort() {
		stop();
		m_Builder = new AlertDialog.Builder(m_Context);
		m_Builder.setTitle("選擇動作");
		m_Builder.setMessage("是否中止更新");
		m_Builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				clear();
				m_Time = 0;
				m_HandlerTimeOut.removeCallbacks(m_BoxTimeOut);
				log(true);
				log("Abort");
				m_BtnCommandOTA.setVisibility(View.VISIBLE);
				m_Handler.removeCallbacksAndMessages(null);
				m_HandlerTimeOut.removeCallbacksAndMessages(null);
			}
		});
		m_Builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				m_Time = 0;
				m_Handler.postDelayed(m_Runnable, m_RunnableTime);
				m_HandlerTimeOut.removeCallbacks(m_BoxTimeOut);
			}
		});
		m_AlertDialog = m_Builder.create();
		m_AlertDialog.show();
	}


}