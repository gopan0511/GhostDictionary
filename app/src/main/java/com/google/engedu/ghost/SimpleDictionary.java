/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private ArrayList<String> even;
    private ArrayList<String> odd;
    private Random random = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        even = new ArrayList<>();
        odd = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH) {
                words.add(line.trim());
                if(word.length() % 2 == 0) // Even length
                    even.add(word);
                else                       //Odd length
                    odd.add(word);
            }
        }
        Collections.sort(words);
    }

    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix == null){
            //return random word
            return words.get(new Random().nextInt(words.size()));
        }else{
            //has a possible valid word? return word : null
            Log.d("Prefix",prefix);
            return getGoodWordStartingWith(prefix);
        }

    }

    String binarySearch(ArrayList<String> dictionary,String prefix){
        int first = 0;
        int last = words.size()-1;

        while(first <= last){
            int middle = (first+last)/2;
            String middleWord = words.get(middle);
            if (middleWord.startsWith(prefix)){
                return middleWord;
            }
            else if(middleWord.compareToIgnoreCase(prefix) > 0){
                //check left half
                last = middle - 1;
            }else{
                //check right half
                first = middle + 1;
            }
        }
        return null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String word;
        Log.d("Prefix",prefix);
        if(prefix.length() % 2 == 0){
            word = binarySearch(even,prefix);
            return word != null ? word : binarySearch(odd,prefix);
        }
        else {
            word = binarySearch(odd,prefix);
            return word != null ? word : binarySearch(even,prefix);
        }

    }
}
