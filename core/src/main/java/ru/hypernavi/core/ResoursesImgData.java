package ru.hypernavi.core;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Константин on 15.08.2015.
 */
public class ResoursesImgData implements ImgHolder {
    private static final Log LOG = LogFactory.getLog(ResoursesImgData.class);
    public ResoursesImgData() {
        md5Image = new TreeMap<>();

        try {
            final String jsonFile = IOUtils.toString(getClass().getResourceAsStream("/schemaPath.json"));
            final JSONArray paths = (new JSONObject(jsonFile)).getJSONArray("imagePath");
            for (int i = 0; i < paths.length(); ++i) {
                final String pathScheme = paths.getString(i);
                final byte[] result = IOUtils.toByteArray(getClass().getResourceAsStream(pathScheme));
                md5Image.put("/" + ImageHash.generate(result) + ".jpg", result);
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Nullable
    @Override
    public byte[] getImage(final String md5Hash) {
        if (md5Image.containsKey(md5Hash)) {
            return md5Image.get(md5Hash);
        }
        return null;
    }

    private final Map<String, byte[]> md5Image;
}
