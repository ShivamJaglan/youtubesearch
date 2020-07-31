package com.example.timefighter.youtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SearchView searchInput;
    private ListView videosFound;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchInput = (SearchView) findViewById(R.id.search_input);
        videosFound = (ListView) findViewById(R.id.videos_found);
//        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//                }
//            }
//        });
    searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            final Thread thread = new Thread() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        String getSearchText = searchInput.getQuery().toString();
                        if (getSearchText.length() > 3) {
                            searchOnYoutube(getSearchText);
                        } else {
                            if (searchResults != null) {
                                searchResults.clear();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateVideosFound();
                                }

                            });
                        }
                    }
                }
            };
            thread.start();
            return false;

        }
    });
        handler = new Handler();

//        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    searchOnYoutube(v.getText().toString());
//                    return false;
//                }
//                return true;
//            }
//        });

    }

    private List<VideoItem> searchResults;

    private void searchOnYoutube(final String keywords) {
        new Thread() {
            public void run() {
                YouTubeConnector yc = new YouTubeConnector(MainActivity.this);
                searchResults = yc.search(keywords);
                handler.post(new Runnable() {
                    public void run() {
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.videoitem, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.videoitem, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);

                Picasso.get().load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosFound.setAdapter(adapter);
    }

//    private boolean onQueryTextChange(final String newText) {
//        final Thread thread = new Thread() {
//
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                } finally {
//                    String getSearchText = searchInput.getQuery().toString();
//                    if (getSearchText.length() > 2) {
//                        searchOnYoutube(getSearchText);
//                    } else {
//                        if (searchResults != null) {
//                            searchResults.clear();
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateVideosFound();
//                            }
//
//                        });
//                    }
//                }
//            }
//        };
//        thread.start();
//        return false;
//
//    }
}