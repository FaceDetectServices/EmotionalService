package com.yoshio3.services.entities;

/**
 * Created by yoterada on 2016/10/26.
 */


import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yoshio Terada
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EmotionResponseJSONBody {
    private Map<String, Object> faceRectangle;
    private Map<String, Object> scores;

    /**
     * @return the faceRectangle
     */
    public Map<String, Object> getFaceRectangle() {
        return faceRectangle;
    }

    /**
     * @param faceRectangle the faceRectangle to set
     */
    public void setFaceRectangle(Map<String, Object> faceRectangle) {
        this.faceRectangle = faceRectangle;
    }

    /**
     * @return the scores
     */
    public Map<String, Object> getScores() {
        return scores;
    }

    /**
     * @param scores the scores to set
     */
    public void setScores(Map<String, Object> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "EmotionResponseJSONBody{" + "faceRectangle=" + faceRectangle + ", scores=" + scores + '}';
    }

}

