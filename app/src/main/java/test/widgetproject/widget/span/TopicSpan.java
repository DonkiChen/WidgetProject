package test.widgetproject.widget.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.ForegroundColorSpan;

import java.io.Serializable;

public class TopicSpan extends ForegroundColorSpan {
    public static final Parcelable.Creator<TopicSpan> CREATOR = new Creator<TopicSpan>() {
        @Override
        public TopicSpan createFromParcel(Parcel source) {
            return new TopicSpan(source.readInt());
        }

        @Override
        public TopicSpan[] newArray(int size) {
            return new TopicSpan[size];
        }
    };
    private TopicObject topic;

    private TopicSpan(int color) {
        super(color);
    }

    public TopicSpan(int color, TopicObject topic) {
        super(color);
        this.topic = topic;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public TopicObject getTopic() {
        return topic;
    }

    public String getTopicContent() {
        return topic.getPreRule() + topic.getContent() + topic.getSufRule();
    }

    public static class TopicObject implements Serializable, Parcelable {
        private String partyId;
        private String content;
        private String preRule = "";
        private String sufRule = "";

        public TopicObject(String content) {
            this.content = content;
        }

        public TopicObject(String preRule, String content) {
            this.preRule = preRule;
            this.content = content;
        }

        public TopicObject(String preRule, String content, String sufRule) {
            this.content = content;
            this.preRule = preRule;
            this.sufRule = sufRule;
        }

        public TopicObject(String partyId, String preRule, String content, String sufRule) {
            this.partyId = partyId;
            this.content = content;
            this.preRule = preRule;
            this.sufRule = sufRule;
        }

        public String getPartyId() {
            return partyId == null ? "" : partyId;
        }

        public void setPartyId(String partyId) {
            this.partyId = partyId;
        }

        public String getContent() {
            return content == null ? "" : content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPreRule() {
            return preRule == null ? "" : preRule;
        }

        public void setPreRule(String preRule) {
            this.preRule = preRule;
        }

        public String getSufRule() {
            return sufRule == null ? "" : sufRule;
        }

        public void setSufRule(String sufRule) {
            this.sufRule = sufRule;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.partyId);
            dest.writeString(this.content);
            dest.writeString(this.preRule);
            dest.writeString(this.sufRule);
        }

        protected TopicObject(Parcel in) {
            this.partyId = in.readString();
            this.content = in.readString();
            this.preRule = in.readString();
            this.sufRule = in.readString();
        }

        public static final Creator<TopicObject> CREATOR = new Creator<TopicObject>() {
            @Override
            public TopicObject createFromParcel(Parcel source) {
                return new TopicObject(source);
            }

            @Override
            public TopicObject[] newArray(int size) {
                return new TopicObject[size];
            }
        };
    }
}
