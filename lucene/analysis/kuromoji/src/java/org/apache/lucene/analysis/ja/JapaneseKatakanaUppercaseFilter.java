/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * A {@link TokenFilter} that normalizes small letters (捨て仮名) in katakana into normal letters. For
 * instance, "ストップウォッチ" will be translated to "ストツプウオツチ".
 *
 * <p>This filter is useful if you want to search against old style Japanese text such as patents,
 * legal, contract policies, etc.
 */
public final class JapaneseKatakanaUppercaseFilter extends TokenFilter {
  private static final Map<Character, Character> s2l;

  static {
    // supported characters are:
    // ァ ィ ゥ ェ ォ ヵ ㇰ ヶ ㇱ ㇲ ッ ㇳ ㇴ ㇵ ㇶ ㇷ ㇷ゚ ㇸ ㇹ ㇺ ャ ュ ョ ㇻ ㇼ ㇽ ㇾ ㇿ ヮ
    s2l =
        Map.ofEntries(
            Map.entry('ァ', 'ア'),
            Map.entry('ィ', 'イ'),
            Map.entry('ゥ', 'ウ'),
            Map.entry('ェ', 'エ'),
            Map.entry('ォ', 'オ'),
            Map.entry('ヵ', 'カ'),
            Map.entry('ㇰ', 'ク'),
            Map.entry('ヶ', 'ケ'),
            Map.entry('ㇱ', 'シ'),
            Map.entry('ㇲ', 'ス'),
            Map.entry('ッ', 'ツ'),
            Map.entry('ㇳ', 'ト'),
            Map.entry('ㇴ', 'ヌ'),
            Map.entry('ㇵ', 'ハ'),
            Map.entry('ㇶ', 'ヒ'),
            Map.entry('ㇷ', 'フ'),
            Map.entry('ㇸ', 'ヘ'),
            Map.entry('ㇹ', 'ホ'),
            Map.entry('ㇺ', 'ム'),
            Map.entry('ャ', 'ヤ'),
            Map.entry('ュ', 'ユ'),
            Map.entry('ョ', 'ヨ'),
            Map.entry('ㇻ', 'ラ'),
            Map.entry('ㇼ', 'リ'),
            Map.entry('ㇽ', 'ル'),
            Map.entry('ㇾ', 'レ'),
            Map.entry('ㇿ', 'ロ'),
            Map.entry('ヮ', 'ワ'));
  }

  private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);

  public JapaneseKatakanaUppercaseFilter(TokenStream input) {
    super(input);
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      String term = termAttr.toString();
      // Small letter "ㇷ゚" is not single character, so it should be converted to "プ" as String
      term = term.replace("ㇷ゚", "プ");
      char[] src = term.toCharArray();
      char[] result = new char[src.length];
      for (int i = 0; i < src.length; i++) {
        Character c = s2l.get(src[i]);
        if (c != null) {
          result[i] = c;
        } else {
          result[i] = src[i];
        }
      }
      String resultTerm = String.copyValueOf(result);
      termAttr.setEmpty().append(resultTerm);
      return true;
    } else {
      return false;
    }
  }
}
