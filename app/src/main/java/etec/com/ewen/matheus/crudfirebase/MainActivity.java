package etec.com.ewen.matheus.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import etec.com.ewen.matheus.crudfirebase.modelo.Pessoa;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtNome, edtEmail;
    Button btnCad, btnAtu, btnDel, btnPes;
    ListView lstDados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    Pessoa pessoaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        btnCad = findViewById(R.id.btnCadastra);
        btnAtu = findViewById(R.id.btnAtualiza);
        btnDel = findViewById(R.id.btnApaga);
        lstDados = findViewById(R.id.lstDados);
        btnPes = findViewById(R.id.btnPesquisa);

        btnCad.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnAtu.setOnClickListener(this);
        btnPes.setOnClickListener(this);

        inicializarFirebase();
        eventoDatabase();

        btnAtu.setEnabled(false);
        btnDel.setEnabled(false);

        lstDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa)parent.getItemAtPosition(position);
                edtNome.setText(pessoaSelecionada.getNome());
                edtEmail.setText(pessoaSelecionada.getEmail());
                btnAtu.setEnabled(true);
                btnDel.setEnabled(true);
                btnCad.setEnabled(false);
            }
        });
    }


    private void eventoDatabase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPessoa.clear();
                for(DataSnapshot objSnapshot:snapshot.getChildren()){
                    Pessoa p = objSnapshot.getValue(Pessoa.class);
                    listPessoa.add(p);
                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this, android.R.layout.simple_list_item_1, listPessoa);
                lstDados.setAdapter(arrayAdapterPessoa);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //para a lista
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }

    @Override
    public void onClick(View view) {
        final Pessoa p = new Pessoa();
        switch (view.getId()) {
            case R.id.btnCadastra:
                p.setId(UUID.randomUUID().toString());
                p.setNome(edtNome.getText().toString());
                p.setEmail(edtEmail.getText().toString());
                databaseReference.child("Pessoa").child(p.getId()).setValue(p);
                limparCampos();
                Toast.makeText(this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAtualiza:
                p.setId(pessoaSelecionada.getId());
                p.setNome(edtNome.getText().toString().trim());
                p.setEmail(edtEmail.getText().toString().trim());
                databaseReference.child("Pessoa").child(p.getId()).setValue(p);
                limparCampos();
                Toast.makeText(this, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                btnCad.setEnabled(true);
                btnAtu.setEnabled(false);
                btnDel.setEnabled(false);
                break;
                case R.id.btnApaga:
                    AlertDialog.Builder confirmacao = new AlertDialog.Builder(MainActivity.this);
                    confirmacao.setTitle("Excluir");
                    confirmacao.setMessage("Deseja realmente excluir?");
                    confirmacao.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            p.setId(pessoaSelecionada.getId());
                            databaseReference.child("Pessoa").child(p.getId()).removeValue();
                            limparCampos();
                            Toast.makeText(MainActivity.this, "Excluido com sucesso", Toast.LENGTH_SHORT).show();
                            btnCad.setEnabled(true);
                            btnAtu.setEnabled(false);
                            btnDel.setEnabled(false);
                        }
                    });
                    confirmacao.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Exclusão cancelada", Toast.LENGTH_SHORT).show();
                            btnCad.setEnabled(true);
                            btnAtu.setEnabled(false);
                            btnDel.setEnabled(false);
                            limparCampos();

                        }
                    });
                    confirmacao.show();
                    break;
            case R.id.btnPesquisa:
                Intent abrirPesquisa = new Intent(MainActivity.this, Pesquisar.class);
                startActivity(abrirPesquisa);
                break;

        }
}
    private void limparCampos() {
        edtNome.setText("");
        edtEmail.setText("");
        edtNome.requestFocus();
    }
}