package com.example.georgeg308.dogapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    Button btnHit;
    Button btnBreed;
    TextView txtJson, lblBreed;
    TextView txtBreed;
    ProgressDialog pd;
    ImageView dogImage;
    Bitmap dogBmp;




    public DogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DogFragment newInstance(String param1) {
        DogFragment fragment = new DogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dog, container, false);
        lblBreed =view.findViewById(R.id.lblBreed);
        btnHit = (Button) view.findViewById(R.id.buttonRandom);
        txtJson = (TextView) view.findViewById(R.id.txtJson);
        dogImage = view.findViewById(R.id.dogImageView);
        txtBreed = view.findViewById(R.id.txtBreed);
        btnBreed = view.findViewById(R.id.buttonBreed);
        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonTask().execute("https://dog.ceo/api/breeds/image/random");
            }
        });

        Log.d("*********************", "onCreateView: "+ mParam1);
        txtBreed.setText(mParam1);

        btnBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String breeds = txtBreed.getText().toString();

                breeds = breeds.toLowerCase();
                breeds = breeds.replaceAll("\\s+","");
                Log.d("Click", "onClick: " + breeds);
                Log.d("#######################", "https://dog.ceo/api/breed/"+ breeds +"/images/random");
                new JsonTask().execute("https://dog.ceo/api/breed/"+ breeds +"/images/random");
            }
        });

        final EditText editText = (EditText) view.findViewById(R.id.txtBreed);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    btnBreed.callOnClick();
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    handled = true;
                }
                return handled;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (dogImage.getDrawable() == null) {
                    Snackbar.make(view, "There is no image to add to favorite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.d("!!!!!!!!!!!!", "onClick: No Image");
                } else {

                    String dUrl = txtJson.getText().toString();
                    String[] split = dUrl.split("\\/");
                    String breed = split[split.length - 2];

                    Log.d("BREED", "onClick:           " + breed);

                    Snackbar.make(view, "Image added to favorite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    DogDbHelper db = new DogDbHelper(getContext());

                    //db.dropTable();

                    // get image from drawable
                    Bitmap image = ((BitmapDrawable) dogImage.getDrawable()).getBitmap();
                    // convert bitmap to byte
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte imageInByte[] = stream.toByteArray();

                    // Inserting Contacts
                    Log.d("Insert: ", "Inserting ..");
                    db.addDog(new DogObject(dUrl, imageInByte, breed));

                    // Reading all data from database
                    List<DogObject> dogs = db.getAllDogs();
                    for (DogObject dg : dogs) {
                        String log = "ID:" + dg.id + " URL: " + dg.url
                                + " ,Image: " + dg.image.toString()  + " ,Breed: " + dg.breed;
                        Log.d("DB",log);
                    }


                }
            }

            });

        FloatingActionButton fabShare = (FloatingActionButton) view.findViewById(R.id.fabShare);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dogImage.getDrawable() == null) {
                    Snackbar.make(view, "There is no image to share", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.d("!!!!!!!!!!!!", "onClick: No Image");
                } else {


                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = txtJson.getText().toString();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sending a nice doggo");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }

            }

        });




        return view;
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getActivity());
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                String jsonString = buffer.toString();
                Log.d("1111111111111", jsonString);
                JSONObject jObj = new JSONObject(jsonString);
                String imageUrl = jObj.getString("message");
                Log.d("2222222222222222", imageUrl);
                URL dogUrl = new URL(imageUrl);
                Log.d("3333333333333", dogUrl.toString());
                Bitmap bmp = BitmapFactory.decodeStream(dogUrl.openConnection().getInputStream());
                Log.d("4444444444444", "doInBackground: here");
                //dogImage.setImageBitmap(bmp);
                dogBmp = bmp;


                return imageUrl;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("0000000000000000000", "doInBackground: suuuuuuuuuuuuuuuuuuuuup");

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if(result != null) {
                txtJson.setText(result);
                dogImage.setImageBitmap(dogBmp);
                String[] split = result.split("\\/");
                String breed = split[split.length - 2];
                lblBreed.setText(breed);

                Linkify.addLinks(txtJson,Linkify.WEB_URLS);

            }else
            {
                lblBreed.setText("");
                txtJson.setText("Could not retrieve image or could not find entered breed");
                dogImage.setImageResource(0);
            }

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction();
    }
}


