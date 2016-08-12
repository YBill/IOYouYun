package com.ioyouyun.receivemsg.msg;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 卫彪 on 2016/7/14.
 */
public class MsgBodyTemplate implements Parcelable{

    protected MsgBodyTemplate(Parcel in) {
        desc = in.readString();
        business_id = in.readString();
        type = in.readString();
        href = in.readString();
        push_desc = in.readString();
    }

    public static final Creator<MsgBodyTemplate> CREATOR = new Creator<MsgBodyTemplate>() {
        @Override
        public MsgBodyTemplate createFromParcel(Parcel in) {
            return new MsgBodyTemplate(in);
        }

        @Override
        public MsgBodyTemplate[] newArray(int size) {
            return new MsgBodyTemplate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(desc);
        dest.writeString(business_id);
        dest.writeString(type);
        dest.writeString(href);
        dest.writeString(push_desc);
    }

    private int isLook; // 是否已经查看该通知  0：看过 1：没看过
    private String date; // 时间
    private String desc;
    private SourceEntity source;
    private String business_id;
    private ActionEntity action;
    private ObjectEntity object;
    private String type;
    private String href;
    private ExtEntity ext;
    private String push_desc;

    public String getDate() {
        return date;
    }

    public MsgBodyTemplate() {
    }

    public int getIsLook() {
        return isLook;
    }

    public void setIsLook(int isLook) {
        this.isLook = isLook;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public ActionEntity getAction() {
        return action;
    }

    public void setAction(ActionEntity action) {
        this.action = action;
    }

    public ObjectEntity getObject() {
        return object;
    }

    public void setObject(ObjectEntity object) {
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public ExtEntity getExt() {
        return ext;
    }

    public void setExt(ExtEntity ext) {
        this.ext = ext;
    }

    public String getPush_desc() {
        return push_desc;
    }

    public void setPush_desc(String push_desc) {
        this.push_desc = push_desc;
    }

    public static class SourceEntity {
        private String id;
        private String desc;
        private String type;
        private String href;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    public static class ActionEntity {
        private String lable_after_processed;
        private String label;
        private int status;
        private String href;

        private List<ParametersEntity> parameters;

        public String getLable_after_processed() {
            return lable_after_processed;
        }

        public void setLable_after_processed(String lable_after_processed) {
            this.lable_after_processed = lable_after_processed;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public List<ParametersEntity> getParameters() {
            return parameters;
        }

        public void setParameters(List<ParametersEntity> parameters) {
            this.parameters = parameters;
        }

        public static class ParametersEntity {
            private String value;
            private String name;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    public static class ObjectEntity {
        private String id;
        private String desc;
        private String type;
        private String href;
        private ExtEntity ext;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public ExtEntity getExt() {
            return ext;
        }

        public void setExt(ExtEntity ext) {
            this.ext = ext;
        }

        public static class ExtEntity {
            private String groupType;
            private int groupCommunityModel;
            private String groupModel;
            private int members;
            private int maxMembers;
            private List<String> admins;

            public String getGroupType() {
                return groupType;
            }

            public void setGroupType(String groupType) {
                this.groupType = groupType;
            }

            public int getGroupCommunityModel() {
                return groupCommunityModel;
            }

            public void setGroupCommunityModel(int groupCommunityModel) {
                this.groupCommunityModel = groupCommunityModel;
            }

            public String getGroupModel() {
                return groupModel;
            }

            public void setGroupModel(String groupModel) {
                this.groupModel = groupModel;
            }

            public int getMembers() {
                return members;
            }

            public void setMembers(int members) {
                this.members = members;
            }

            public int getMaxMembers() {
                return maxMembers;
            }

            public void setMaxMembers(int maxMembers) {
                this.maxMembers = maxMembers;
            }

            public List<String> getAdmins() {
                return admins;
            }

            public void setAdmins(List<String> admins) {
                this.admins = admins;
            }
        }
    }

    public static class ExtEntity {
        private String msg;
        private String groupModel;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getGroupModel() {
            return groupModel;
        }

        public void setGroupModel(String groupModel) {
            this.groupModel = groupModel;
        }
    }
}
