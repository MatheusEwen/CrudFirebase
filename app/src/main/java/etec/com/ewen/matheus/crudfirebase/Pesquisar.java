package etec.com.ewen.matheus.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import etec.com.ewen.matheus.crudfirebase.modelo.Pessoa;

public class Pesquisar extends AppCompatActivity {

    private EditText edtPesquisa;
    private ListView lstPesquisa;
    //OBJETOS PARA FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //PARA CARREGAR OS DADOS NA LISTA
    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);

        edtPesquisa = (EditText) findViewById(R.id.edtPesquisa);
        lstPesquisa = (ListView) findViewById(R.id.lstPesquisa);
        inicializarFirebase();
        fazerPesquisa();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(Pesquisar.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //PARA A LISTA
        databaseReference = firebaseDatabase.getReference();

    }

    private void pesquisarNome(String busca){
        Query consulta;
        if(busca.equals("")){
            consulta = databaseReference.child("Pessoa").orderByChild("nome");

        } else{
            //SELECT *FROM Pessoa WHERE nome = busca
            // usar equalTo(busca) para pesquisa EXATA
            // para consulta tipo Like, usar o código abaixo
            // SELECT * FROM Pessoa WHERE nome LIKE %busca%
            consulta = databaseReference.child("Pessoa").orderByChild("nome")
                    .startAt(busca).endAt(busca+"\uf8ff");
        }
        listPessoa.clear();
        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Pessoa p = objSnapshot.getValue(Pessoa.class);
                    listPessoa.add(p);
                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(Pesquisar.this, android.R.layout.simple_list_item_1,listPessoa);
                lstPesquisa.setAdapter(arrayAdapterPessoa);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});

    }

    private void fazerPesquisa() {
        edtPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String busca = edtPesquisa.getText().toString().trim();
                pesquisarNome(busca);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pesquisarNome("");
    }
}