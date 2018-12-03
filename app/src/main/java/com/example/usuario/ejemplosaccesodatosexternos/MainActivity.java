package com.example.usuario.ejemplosaccesodatosexternos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {
Button buttonCSV, buttonXML, buttonJSON;
ListView listView;
ProgressDialog progressDialog = null;
static String SERVIDOR = "http://192.168.44.1/scripts/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonCSV = findViewById(R.id.buttonCSV);
        buttonXML =findViewById(R.id.buttonXML);
        buttonJSON = findViewById(R.id.buttonJSON);
        listView = findViewById(R.id.lista);


        buttonJSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarJSON descargarJSON = new DescargarJSON();
                descargarJSON.execute("consultaJSON.php");
            }
        });

        buttonCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              DescargarCSV descargarCSV = new DescargarCSV();
              descargarCSV.execute("consultaCSV.php");



            }
        });


        buttonXML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarXML descargarXML=new DescargarXML();
                descargarXML.execute("consultaXML.php");
            }
        });

    }







    private class DescargarJSON extends AsyncTask<String, Void, Void> {
        List<String> list = new ArrayList<>();

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            String url = SERVIDOR + script;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String contenido="";
            URLConnection conexion=null;

            try {
                conexion = new URL(url).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                conexion.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream inputStream = null;
            try {
                inputStream = conexion.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String linea = "";

            try {

                while ((linea = br.readLine()) != null) {
                    contenido += linea;
                }

            }catch (IOException e){}

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            list.add("id");
            list.add("Nombre");
            list.add("Apellidos");
            list.add("Telefono");

            JsonParser parser = new JsonParser();

            JsonArray jsonArray = parser.parse(contenido).getAsJsonArray();

            String[] fila = new String[4];

            for (JsonElement elemento : jsonArray) {
                JsonObject objeto = elemento.getAsJsonObject();

                fila[0] = objeto.get("id").getAsString();
                fila[1] = objeto.get("nombre").getAsString();
                fila[2] = objeto.get("apellidos").getAsString();
                fila[3] = objeto.get("telefono").getAsString();

                list.add(String.valueOf(fila));
            }

            return null;
        }
            @Override
            protected void onPostExecute (Void aVoid){
                super.onPostExecute(aVoid);
                ArrayAdapter<String> adapter;

                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, (List<String>) list);
                listView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            protected void onPreExecute () {
                super.onPreExecute();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setTitle("Descargando datos...");
                progressDialog.show();
            }
        }









    private class DescargarXML extends AsyncTask<String, Void, Void> {
        List<String> list = new ArrayList<String>();
        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            String url = SERVIDOR+ script;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new URL(url).openStream());

                Element raiz = doc.getDocumentElement();
                System.out.println("Raíz: " + raiz.getNodeName());
                NodeList hijos = raiz.getChildNodes();

                for (int i = 0; i < hijos.getLength(); i++) {
                    Node nodo = hijos.item(i);
                    if (nodo instanceof Element) {
                        NodeList nietos = nodo.getChildNodes();

                        System.out.println("Nietos:" + nietos.getLength());

                        String registro = "";
                        for (int j = 0; j < nietos.getLength(); j++) {
                            if (nietos.item(j) instanceof Element) {

                                System.out.println("" + nietos.item(j).getNodeName() + " " + nietos.item(j).getTextContent());
                                registro+=" " + nietos.item(j).getNodeName() + " " + nietos.item(j).getTextContent();

                            }
                        }
                        list.add(registro);
                    }
                }

            } catch (ParserConfigurationException ex) {
            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
            } catch (SAXException ex) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> adapter;

            adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,list);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando datos...");
            progressDialog.show();
        }
    }

    private class DescargarCSV extends AsyncTask<String, Void, Void>{
String total ="";
        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            URL url = null;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(SERVIDOR+script);
                System.out.println(SERVIDOR+script);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader br = null;

                    br = new BufferedReader(new InputStreamReader(inputStream));

                            String linea;

                    while ((linea = br.readLine()) != null) {
                        total += linea+"\n";
                    }

                    br.close();
                    inputStream.close();

                }

            } catch (IOException e) {
             e.printStackTrace();
             }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> adapter;
            List<String> list = new ArrayList<String>();

            String[] lineas = total.split("\n");
            for (String lin: lineas){
                String[] campos = lin.split(",");
                String registro = "";
                registro = "ID: "+ campos[0];
                registro += "NOMBRE: "+campos[1];
                registro += "APELLIDOS: "+campos[2];
                registro += "TEL: "+campos[3];
                list.add(registro);

            }

            adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,list);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando datos...");
            progressDialog.show();

        }
    }
}
