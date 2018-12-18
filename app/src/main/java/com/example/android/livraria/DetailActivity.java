package com.example.android.livraria;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.livraria.data.BooksContract.BookEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private static final int MY_PERMISSIONS_REQUEST_CALL = 0;

    private Uri currentBookUri;

    private EditText bookName;
    private EditText bookPrice;
    private EditText bookQuantity;
    private EditText bookSupName;
    private EditText bookSupPhone;
    private ImageView phoneCall;
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        bookName = findViewById(R.id.itemNome);
        bookPrice = findViewById(R.id.itemPreco);
        bookQuantity = findViewById(R.id.itemQuantidade);
        bookSupName = findViewById(R.id.fornecedorNome);
        bookSupPhone = findViewById(R.id.fornecedorTelefone);
        phoneCall = findViewById(R.id.phoneCall);

        bookName.setOnTouchListener(mTouchListener);
        bookPrice.setOnTouchListener(mTouchListener);
        bookQuantity.setOnTouchListener(mTouchListener);
        bookSupName.setOnTouchListener(mTouchListener);
        bookSupPhone.setOnTouchListener(mTouchListener);

        phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + bookSupPhone.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(DetailActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL);
                }
            }
        });

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE,
        };

        return new CursorLoader(this,
                currentBookUri,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            // Acha as colunas de atributos pet em que estamos interessados
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // Extrai o valor do Cursor para o índice de coluna dado
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            int supPhone = cursor.getInt(supPhoneColumnIndex);

            bookName.setText(name);
            bookPrice.setText(Integer.toString(price));
            bookQuantity.setText(Integer.toString(quantity));
            bookSupName.setText(supName);
            bookSupPhone.setText(Integer.toString(supPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookName.setText("");
        bookPrice.setText("");
        bookQuantity.setText("");
        bookSupName.setText("");
        bookSupPhone.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                update();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    // Navigate back to parent activity (MainActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // Caso contrário, se houver alterações não salvas, configura um diálogo para alertar o usuário.
                // Cria um click listener para lidar com o usuário, confirmando que
                // mudanças devem ser descartadas.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Usuário clidou no botão "Discard", e navegou para a activity pai.
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                // Mostra um diálogo que notifica o usuário de que há alterações não salvas
                showUnsavedChangesDialog(discardButtonClickListener);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // Se o pet não mudou, continue lidando com clique do botão "back"
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Caso contrário, se houver alterações não salvas, configure uma caixa de diálogo para alertar o usuário.
        // Crie um click listener para lidar com o usuário, confirmando que mudanças devem ser descartadas.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicou no botão "Discard", fecha a activity atual.
                        finish();
                    }
                };

        // Mostra o diálogo que diz que há mudanças não salvas
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void update() {
        String name = bookName.getText().toString().trim();
        String priceString = bookPrice.getText().toString().trim();
        String quantityString = bookQuantity.getText().toString().trim();
        String supName = bookSupName.getText().toString().trim();
        String phoneString = bookSupPhone.getText().toString().trim();

        if (dadosValidos(name, priceString, quantityString, supName, phoneString)) {
            int price = Integer.parseInt(priceString);
            int quantity = Integer.parseInt(quantityString);
            int phone = Integer.parseInt(phoneString);

            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, name);
            values.put(BookEntry.COLUMN_BOOK_PRICE, price);
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supName);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE, phone);

            int rowsUpdated = getContentResolver().update(currentBookUri, values, null, null);
            if (rowsUpdated == 0) {
                Toast.makeText(this, getString(R.string.errorUpdating), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.updated), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.errorDeleting), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean dadosValidos(String name, String price, String quantity, String supName, String phone) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) ||
                TextUtils.isEmpty(quantity) || TextUtils.isEmpty(supName) ||
                TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.incorretValues, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Cria um AlertDialog.Builder e configura a mensagem e click listeners
        // para os botões positivos e negativos do dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // O usuário clicou no botão "Continuar editando", então, feche a caixa de diálogo
                // e continue editando o pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Cria e mostra o AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
