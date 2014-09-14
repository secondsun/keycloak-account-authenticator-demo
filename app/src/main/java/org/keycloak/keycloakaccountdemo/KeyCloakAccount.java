package org.keycloak.keycloakaccountdemo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.impl.http.HttpRestProvider;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


public class KeyCloakAccount extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_cloak_account);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.key_cloak_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_key_cloak_account, container, false);
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            final AccountManager am = AccountManager.get(getActivity());
            final Account account = am.getAccountsByType("org.keycloak.Account")[0];
            fetchAccountInfo(account);
        }

        private void fetchAccountInfo(final Account account) {

            final AccountManager am = AccountManager.get(getActivity());

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {

                    URL accountUrl = null;
                    try {
                        accountUrl = new URL("https://auth-coffeeregister.rhcloud.com/auth/realms/sup/account");

                        String token = am.getAuthToken(account, "org.keycloak.Account.token", null, null, null, null).getResult().getString(AccountManager.KEY_AUTHTOKEN);
                        HttpRestProvider provider = new HttpRestProvider(accountUrl);
                        provider.setDefaultHeader("Authorization", "bearer " + token);
                        HeaderAndBody accountData = provider.get();
                        String accountBody = new String(accountData.getBody());

                        return accountBody;

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                }
            }.execute((Void[]) null);
        }

    }
}
