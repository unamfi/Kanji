package unam.mobi.kanji.dibujado;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import unam.mobi.kanji.R;
import unam.mobi.kanji.dialogos.Dialogo_Guardar;
import unam.mobi.kanji.dialogos.Dialogo_Guardar.OnGuardar;
import unam.mobi.kanji.dibujado.Lienzo.OnMiEscuchador;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Fragment_Lienzo extends SherlockFragment implements
		OnClickListener {

	private View view;
	private ArrayList<Integer> guias;
	private ArrayList<Integer> puntos;

	private Lienzo lienzo;
	private ListenerDialogo listenerDialogo;

	private LinearLayout layout;

	private final int ID_BORRAR_COM = 1;
	private final int ID_ACEPTA_MIT = 2;
	private final int ID_BORRAR_MIT = 3;
	private final int ID_MENU_GUARDAR = 4;
	private int porcentaje;

	private boolean menu_visible;
	private boolean es_juego;

	public Fragment_Lienzo(ArrayList<Integer> guias, ArrayList<Integer> puntos,
			boolean es_juego) {
		this.guias = guias;
		this.puntos = puntos;
		this.es_juego = es_juego;
		porcentaje = 0;
		menu_visible = false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			listenerDialogo = (ListenerDialogo) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	public interface ListenerDialogo {
		public void crea_Dialogo_Porcen(int porcentaje);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		setHasOptionsMenu(true);

		view = inflater.inflate(R.layout.lienzo, container, false);

		layout = (LinearLayout) view.findViewById(R.id.layout_botones);


		return view;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		crear_boton_borrar();

		crear_Lienzo();

		lienzo.inic_Manej_Punt(guias, puntos);
		lienzo.setOnMiEscuchar(new OnMiEscuchador() {

			@Override
			public void inicia_Botones(int porcen) {
				porcentaje = porcen;

				crear_boton_acepborra();
			}
		});
	}

	private void crear_Lienzo() {
		
		RelativeLayout layoutLienzo = (RelativeLayout) view
				.findViewById(R.id.layout_lienzo);
		
		lienzo = new Lienzo(getSherlockActivity());

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE); 
		params.addRule(RelativeLayout.ABOVE, layout.getId()); 
		lienzo.setLayoutParams(params); 
		
		layoutLienzo.addView(lienzo); 

	}
	private void crear_boton_borrar() {

		menu_visible = false;
		getSherlockActivity().supportInvalidateOptionsMenu();

		layout.removeAllViews();

		ImageButton button = new ImageButton(getSherlockActivity());
		button.setId(ID_BORRAR_COM);
		button.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		button.setAdjustViewBounds(true);
		button.setBackgroundColor(Color.TRANSPARENT);
		button.setPadding(0, 0, 0, 0);
		button.setScaleType(ScaleType.FIT_XY);
		button.setImageResource(R.drawable.borrar_kanji_com);
		button.setOnClickListener(this);

		layout.addView(button);
	}

	private void crear_boton_acepborra() {

		menu_visible = true;
		getSherlockActivity().supportInvalidateOptionsMenu();

		layout.removeAllViews();

		ImageButton aceptar = new ImageButton(getSherlockActivity());
		aceptar.setId(ID_ACEPTA_MIT);
		aceptar.setLayoutParams(new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1));
		aceptar.setAdjustViewBounds(true);
		aceptar.setBackgroundColor(Color.TRANSPARENT);
		aceptar.setPadding(0, 0, 0, 0);
		aceptar.setScaleType(ScaleType.FIT_XY);
		aceptar.setImageResource(R.drawable.aceptar_kanji_mit);
		aceptar.setOnClickListener(this);

		layout.addView(aceptar);

		View view = new View(getSherlockActivity());
		LinearLayout.LayoutParams params = new LayoutParams(3,
				LayoutParams.MATCH_PARENT, 0);
		params.setMargins(0, 20, 0, 20);
		view.setLayoutParams(params);
		view.setBackgroundColor(Color.argb(128, 52, 52, 52));

		layout.addView(view);

		ImageButton borrar = new ImageButton(getSherlockActivity());
		borrar.setId(ID_BORRAR_MIT);
		borrar.setLayoutParams(new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1));
		borrar.setAdjustViewBounds(true);
		borrar.setBackgroundColor(Color.TRANSPARENT);
		borrar.setPadding(0, 0, 0, 0);
		borrar.setScaleType(ScaleType.FIT_XY);
		borrar.setImageResource(R.drawable.borrar_kanji_mit);
		borrar.setOnClickListener(this);

		layout.addView(borrar);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case ID_BORRAR_COM:
			lienzo.limpiar_Pantalla();
			break;
		case ID_ACEPTA_MIT:
			listenerDialogo.crea_Dialogo_Porcen(porcentaje);
			break;
		case ID_BORRAR_MIT:
			limpiar_Lienzo();
			break;

		}

	}

	public void limpiar_Lienzo() {
		lienzo.limpiar_Pantalla();
		crear_boton_borrar();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		MenuItem menuItem;
		menuItem = menu.add(0, ID_MENU_GUARDAR, 0, "Guardar");
		menuItem.setIcon(R.drawable.guardar).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menuItem.setVisible(menu_visible && es_juego);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == ID_MENU_GUARDAR) {
			if (verifica_memoria()) {

				crear_Dialog_Guardar();

			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void crear_Dialog_Guardar() {
		Dialogo_Guardar dialogo_Guardar = new Dialogo_Guardar();
		dialogo_Guardar.show(getSherlockActivity().getSupportFragmentManager(),
				"dial");

		dialogo_Guardar.setOnMiEscuchar(new OnGuardar() {

			@Override
			public void guardar(File file) {
				guardar_imagen(file);
			}
		});

	}

	private boolean verifica_memoria() {
		boolean valor;

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			valor = true;
		}

		else {
			valor = false;
			Toast.makeText(getSherlockActivity(),
					"No se puede escribir en la memoria externa",
					Toast.LENGTH_SHORT).show();
		}

		return valor;
	}

	private void guardar_imagen(File file) {

		DialogFragment fragment = (DialogFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag("dial");

		if (fragment != null) {
			fragment.dismiss();
			Log.d("Si existe", "Se borro");
		}

		view.setDrawingCacheEnabled(true);

		Bitmap origi = view.getDrawingCache();

		Bitmap bitmap = Bitmap.createBitmap(origi, 0, 0, origi.getWidth(),
				origi.getHeight() - layout.getHeight());

		try {

			file.createNewFile();
			OutputStream fOut = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();

			// MediaStore.Images.Media.insertImage(this.getContentResolver(),
			// file.getAbsolutePath(), file.getName(), file.getName());
			getSherlockActivity().sendBroadcast(
					new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
							.fromFile(file)));

			Toast.makeText(getSherlockActivity(), "Guardado",
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// Log.e("saveToExternalStorage()", e.getMessage());
		}

		view.setDrawingCacheEnabled(false);

	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		// Log.d("ciclo", "OnCreate_Liezo");
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onActivityCreated(savedInstanceState);
//		// Log.d("ciclo", "OnActivityCreated_Liezo");
//	}
//
//	@Override
//	public void onStart() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		// Log.d("ciclo", "OnStart_Liezo");
//	}
//
//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		// Log.d("ciclo", "OnResumen_Liezo");
//	}
//
//	@Override
//	public void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		// Log.d("ciclo", "OnPause_Liezo");
//	}
//
//	@Override
//	public void onStop() {
//		// TODO Auto-generated method stub
//		super.onStop();
//		// Log.d("ciclo", "OnStop_Liezo");
//	}
//
//	@Override
//	public void onDestroyView() {
//		// TODO Auto-generated method stub
//		super.onDestroyView();
//		// Log.d("ciclo", "OnDestroyView_Liezo");
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		// Log.d("ciclo", "OnDestroy_Liezo");
//	}
//
//	@Override
//	public void onDetach() {
//		// TODO Auto-generated method stub
//		super.onDetach();
//		// Log.d("ciclo", "OnDetach_Liezo");
//	}
}
