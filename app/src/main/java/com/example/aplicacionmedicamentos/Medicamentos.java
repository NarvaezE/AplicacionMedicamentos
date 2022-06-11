package com.example.aplicacionmedicamentos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Medicamentos extends AppCompatActivity {
    private EditText txtnom, txtcan;
    private Spinner spMeds;
    private Button btnagregar;
    private ListView lvMeds;
    private AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamentos);

        txtnom = (EditText) findViewById(R.id.txtnom);
        txtcan = (EditText) findViewById(R.id.txtcan);
        btnagregar=(Button) findViewById(R.id.btnagregar);
        spMeds=(Spinner) findViewById(R.id.spinnerMeds);
        lvMeds=(ListView)findViewById(R.id.lvMeds);
        cliente=new AsyncHttpClient();
        //Arreglo que guarda los elementos de nuestro spinner
        String [] catalogos= {"Seleccionar","Analgésicos","Laxantes", "Antiálergicos"
                , "Antidiarreicos","Antiinflamatorios", "Antiinfecciosos","Mucolitícos"
                , "Antipiréticos", "Antiulcerosos"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,catalogos);
        spMeds.setAdapter(adapter);
        obtenerMedicinas();
        botonAgregar();

    }

    private void botonAgregar(){

        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtnom.getText().toString().isEmpty() || txtcan.getText().toString().isEmpty()){
                    Toast.makeText(Medicamentos.this, "Hay Campos Vacíos!!", Toast.LENGTH_SHORT).show();
                }else if(spMeds.getSelectedItemPosition()==0) {
                    Toast.makeText(Medicamentos.this, "Seleccione una categoria de medicamento!!", Toast.LENGTH_SHORT).show();
                }else{
                    Medicina m = new Medicina();
                    m.setNombre(txtnom.getText().toString().replaceAll(" ","%20"));
                    m.setCantidad(Integer.parseInt(txtcan.getText().toString()));
                    m.setTipoCatalogo(spMeds.getSelectedItemPosition());
                    agregarMedicinas(m);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    obtenerMedicinas();
                }
            }
        });
    } // Cierra el metodo botonAlmacenar.

    private void agregarMedicinas(Medicina m){
        String url = "https://ggabysgs.lucusvirtual.es/insertar_med.php?";
        String parametros = "nombre="+m.getNombre()+"&cantidad="+m.getCantidad()+"&tipoCatalogo="+m.getTipoCatalogo();
        cliente.post(url + parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    Toast.makeText(Medicamentos.this, "Producto Agregar Correctamente!!", Toast.LENGTH_SHORT).show();
                    txtnom.setText("");
                    txtcan.setText("");
                    spMeds.setSelection(0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    } // Cierra el metodo agregarProducto.

//    private void obtenerMedicinas(){
//        String url="https://ggabysgs.lucusvirtual.es/mostrar_medicamentos.php";
//    }
    private void obtenerMedicinas(){
        String url = "https://ggabysgs.lucusvirtual.es/mostrar_medicamentos.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    Toast.makeText(Medicamentos.this, "Entra al status200", Toast.LENGTH_SHORT).show();

                    listarProductos(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    } // Cierra el metodo obtenerProductos.



    private void listarProductos(String respuesta){
        final ArrayList<Medicina> lista = new ArrayList <Medicina> ();
        try{
            JSONArray jsonArreglo = new JSONArray(respuesta);

            for(int i=0;i<jsonArreglo.length();i++){
                Toast.makeText(Medicamentos.this, "Entra al for"+jsonArreglo.length()+"-", Toast.LENGTH_SHORT).show();
                Medicina m = new Medicina();

                m.setId(jsonArreglo.getJSONObject(i).getInt("idMEDICAMENTOS"));
                m.setNombre(jsonArreglo.getJSONObject(i).getString("nombre"));
                m.setCantidad(jsonArreglo.getJSONObject(i).getInt("cantidad"));
                m.setTipoCatalogo(jsonArreglo.getJSONObject(i).getInt("idCatalogo"));
                lista.add(m);

                Toast.makeText(Medicamentos.this, "-"+lista.get(i).toString(), Toast.LENGTH_SHORT).show();
            }

            ArrayAdapter<Medicina> a = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lista);
            lvMeds.setAdapter(a);

            lvMeds.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final Medicina m = lista.get(i);
                    AlertDialog.Builder a = new AlertDialog.Builder(Medicamentos.this);
                    a.setCancelable(true);
                    a.setTitle("PREGUNTA");
                    a.setMessage("¿Desea Eliminar El Producto "+m.getNombre()+"?");

                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String url = "https://ggabysgs.lucusvirtual.es/eliminar_med.php?Id="+m.getId();
                            cliente.post(url, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if(statusCode == 200){
                                        Toast.makeText(Medicamentos.this, "Producto Eliminado Correctamente!!", Toast.LENGTH_SHORT).show();
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        obtenerMedicinas();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                }
                            });

                        }
                    });

                    a.show();
                    return true;
                }
            });

            lvMeds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Medicina m = lista.get(i);

                    StringBuffer b = new StringBuffer();
                    b.append("ID: " + m.getId() + "\n");
                    b.append("NOMBRE: " + m.getNombre() + "\n");
                    b.append("CANTDAD: " + m.getCantidad() + "\n");
                    b.append("CATEGORIA: " + m.getTipoCatalogo());

                    AlertDialog.Builder a = new AlertDialog.Builder(Medicamentos.this);
                    a.setCancelable(true);
                    a.setTitle("Detalle");
                    a.setMessage(b.toString());
                    a.setIcon(R.drawable.ok);
                    a.show();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    } // Cierra el metodo listarProductos.
}