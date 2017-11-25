package cl.moriahdp.tarbaychile.models;

import com.microsoft.projectoxford.vision.contract.Adult;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.contract.Color;
import com.microsoft.projectoxford.vision.contract.Description;
import com.microsoft.projectoxford.vision.contract.Face;
import com.microsoft.projectoxford.vision.contract.ImageType;
import com.microsoft.projectoxford.vision.contract.Metadata;
import com.microsoft.projectoxford.vision.contract.Tag;

import java.util.List;
import java.util.UUID;

/**
 * Created by edwinperaza on 11/25/17.
 */

public class AnalysisImageResult {
    public UUID requestId;
    public Metadata metadata;
    public ImageType imageType;
    public Color color;
    public Adult adult;
    public Description description;
    public List<Category> categories;
    public List<Face> faces;
    public List<Tag> tags;

    public AnalysisImageResult() {
    }
}
