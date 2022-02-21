/*
 * Copyright (c) 2020-2021 CertifAI Sdn. Bhd.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */
package ai.certifai.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.datavec.image.recordreader.objdetect.ImageObject;
import org.datavec.image.recordreader.objdetect.ImageObjectLabelProvider;

public class VocLabelProvider implements ImageObjectLabelProvider {

    private static final String OBJECT_START_TAG = "<object>";
    private static final String OBJECT_END_TAG = "</object>";
    private static final String NAME_TAG = "<name>";
    private static final String XMIN_TAG = "<xmin>";
    private static final String YMIN_TAG = "<ymin>";
    private static final String XMAX_TAG = "<xmax>";
    private static final String YMAX_TAG = "<ymax>";

    private String annotationsDir;

    public VocLabelProvider(@NonNull String baseDirectory){
        this.annotationsDir = FilenameUtils.concat(baseDirectory, "Annotations");

        if(!new File(annotationsDir).exists()){
            throw new IllegalStateException("Annotations directory does not exist. Annotation files should be " +
                    "present at baseDirectory/Annotations/nnnnnn.xml. Expected location: " + annotationsDir);
        }
    }

    @Override
    public List<ImageObject> getImageObjectsForPath(String path) {
        int idx = path.lastIndexOf('/');
        idx = Math.max(idx, path.lastIndexOf('\\'));

        String filename = path.substring(idx+1, path.lastIndexOf('.'));   // e.g. ".folder/folder/folder/image1.jpeg" -> "image1"
        String xmlPath = FilenameUtils.concat(annotationsDir, filename + ".xml");
        File xmlFile = new File(xmlPath);
        if(!xmlFile.exists()){
            throw new IllegalStateException("Could not find XML file for image " + path + "; expected at " + xmlPath);
        }

        String xmlContent;
        try{
            xmlContent = FileUtils.readFileToString(xmlFile, "UTF-8");
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        //Normally we'd use Jackson to parse XML, but Jackson has real trouble with multiple XML elements with
        //  the same name. However, the structure is simple and we can parse it manually (even though it's not
        // the most elegant thing to do :)
        String[] lines = xmlContent.split("\n");

        List<ImageObject> out = new ArrayList<>();
        for( int i=0; i<lines.length; i++ ){
            if(!lines[i].contains(OBJECT_START_TAG)){
                continue;
            }
            String name = null;
            int xmin = Integer.MIN_VALUE;
            int ymin = Integer.MIN_VALUE;
            int xmax = Integer.MIN_VALUE;
            int ymax = Integer.MIN_VALUE;
            while(!lines[i].contains(OBJECT_END_TAG)){
                if(name == null && lines[i].contains(NAME_TAG)){
                    int idxStartName = lines[i].indexOf('>') + 1;
                    int idxEndName = lines[i].lastIndexOf('<');
                    name = lines[i].substring(idxStartName, idxEndName);
                    i++;
                    continue;
                }
                if(xmin == Integer.MIN_VALUE && lines[i].contains(XMIN_TAG)){
                    xmin = extractAndParse(lines[i]);
                    i++;
                    continue;
                }
                if(ymin == Integer.MIN_VALUE && lines[i].contains(YMIN_TAG)){
                    ymin = extractAndParse(lines[i]);
                    i++;
                    continue;
                }
                if(xmax == Integer.MIN_VALUE && lines[i].contains(XMAX_TAG)){
                    xmax = extractAndParse(lines[i]);
                    i++;
                    continue;
                }
                if(ymax == Integer.MIN_VALUE && lines[i].contains(YMAX_TAG)){
                    ymax = extractAndParse(lines[i]);
                    i++;
                    continue;
                }

                i++;
            }

            if(name == null){
                throw new IllegalStateException("Invalid object format: no name tag found for object in file " + xmlPath);
            }
            if(xmin == Integer.MIN_VALUE || ymin == Integer.MIN_VALUE || xmax == Integer.MIN_VALUE || ymax == Integer.MIN_VALUE){
                throw new IllegalStateException("Invalid object format: did not find all of xmin/ymin/xmax/ymax tags in " + xmlPath);
            }

            out.add(new ImageObject(xmin, ymin, xmax, ymax, name));
        }

        return out;
    }

    private int extractAndParse(String line){
        int idxStartName = line.indexOf('>') + 1;
        int idxEndName = line.lastIndexOf('<');
        String substring = line.substring(idxStartName, idxEndName);
        return Integer.parseInt(substring);
    }

    @Override
    public List<ImageObject> getImageObjectsForPath(URI uri) {
        return getImageObjectsForPath(uri.toString());
    }

}
