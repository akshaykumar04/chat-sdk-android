package co.chatsdk.core.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.converter.PropertyConverter;
import org.joda.time.DateTime;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co.chatsdk.core.interfaces.CoreEntity;
import co.chatsdk.core.types.MessageSendStatus;
import co.chatsdk.core.types.MessageType;
import timber.log.Timber;

@org.greenrobot.greendao.annotation.Entity
public class Message implements CoreEntity {

    // TODO: test how this handles timezones
    public static class DateTimeConverter implements PropertyConverter<DateTime, Long> {
        @Override
        public DateTime convertToEntityProperty(Long databaseValue) {
            if (databaseValue == null) {
                return null;
            }

            return new DateTime(databaseValue);
        }

        @Override
        public Long convertToDatabaseValue(DateTime dateTime) {
            return dateTime == null ? null : dateTime.getMillis();
        }
    }

    @Id
    private Long id;

    @Convert(converter = DateTimeConverter.class, columnType = Long.class)
    private DateTime date;
    private Boolean read;
    private String resources;
    private String text;
    private Integer type;
    private Integer status;
    private Long senderId;
    private Long threadId;
    private String entityID;

    @ToOne(joinProperty = "senderId")
    private User Sender;

    @ToOne(joinProperty = "threadId")
    private Thread thread;

    @Transient
    public static final String TAG = Message.class.getSimpleName();

    // We cache the payload to improve performance
    @Transient
    private JSONObject jsonPayload;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 859287859)
    private transient MessageDao myDao;

    @Generated(hash = 1266064432)
    public Message(Long id, DateTime date, Boolean read, String resources, String text, Integer type,
            Integer status, Long senderId, Long threadId, String entityID) {
        this.id = id;
        this.date = date;
        this.read = read;
        this.resources = resources;
        this.text = text;
        this.type = type;
        this.status = status;
        this.senderId = senderId;
        this.threadId = threadId;
        this.entityID = entityID;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    @Generated(hash = 1667105234)
    private transient Long Sender__resolvedKey;

    @Generated(hash = 1974258785)
    private transient Long thread__resolvedKey;

    /** Null safe version of getIsRead*/
    public boolean wasRead() {
        return read != null && read;
    }

    @Override
    public String toString() {
        return String.format("Message, id: %s, type: %s, Sender: %s", id, type, getSender());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityID() {
        return this.entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public DateTime getDate() {
        return this.date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Boolean getIsRead() {
        return this.read;
    }

    public void setIsRead(Boolean isRead) {
        this.read = isRead;
    }

    public String getResources() {
        return this.resources;
    }

    public String getRawJSONPayload() {
        return this.text;
    }

    public void setRawJSONPayload (String payload) {
        this.text = payload;
    }

    public Object valueForKey (String key) {

        try {
            String json = getRawJSONPayload();
            if(json == null || json.length() == 0 ) {
                return "";
            }
            if(jsonPayload == null) {
                jsonPayload = new JSONObject(json);
            }
            if(jsonPayload.has(key)) {
                return  jsonPayload.get(key);
            }
            else {
                return "";
            }
        }
        catch (JSONException e) {
            Timber.v(e.getLocalizedMessage());
//            e.printStackTrace();
            return "";
        }
    }

    public LatLng getLocation () {
        Double latitude = (Double) valueForKey(Keys.MessageLatitude);
        Double longitude = (Double) valueForKey(Keys.MessageLongitude);
        return new LatLng(latitude, longitude);
    }

    public void setValueForKey (Object payload, String key) {
        try {
            if(jsonPayload == null) {
                String jsonString = getRawJSONPayload();
                jsonPayload = jsonString != null ? new JSONObject(jsonString) : new JSONObject();
            }
            jsonPayload.put(key, payload);
            setRawJSONPayload(jsonPayload.toString());
        }
        catch (JSONException e) {
            Timber.v(e.getLocalizedMessage());
//            e.printStackTrace();
        }
    }

    public HashMap<String, Object> values () {
        HashMap<String, Object> values = new HashMap<>();
        try {
            JSONObject json = new JSONObject(getRawJSONPayload());

            for(Iterator<String> iter = json.keys(); iter.hasNext(); ) {
                String key = iter.next();
                values.put(key, json.get(key));
            }
        }
        catch (JSONException e) {
        }
        finally {
            return values;
        }
    }

    /**
     * This is used internally - if you want the message text,
     * you should use getTextString method instead.
     * @return Raw JSON message payload
     */
    public String getText () {
        return getRawJSONPayload();
    }

    public String getTextString() {
        return valueForKey(Keys.MessageText).toString();
    }

    /**
     * This is used internally - if you want to set the message text,
     * you should use setTextString method instead.
     * @param text Raw JSON message payload
     */
    public void setText(String text) {
        setRawJSONPayload(text);
    }
    public void setTextString(String text) {
        setValueForKey(text, Keys.MessageText);
    }

    public Integer getType () {
        return this.type;
    }

    public MessageType getMessageType() {
        if(this.type != null) {
            return MessageType.values()[this.type];
        }
        return MessageType.None;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public void setMessageType(MessageType type) {
        this.type = type.ordinal();
    }

    public Integer getStatus() {
        return this.status;
    }
    public MessageSendStatus getMessageStatus() {
        if(this.status != null) {
            return MessageSendStatus.values()[this.status];
        }
        return MessageSendStatus.None;
    }

    public void setMessageStatus(MessageSendStatus status) {
        this.status = status.ordinal();
    }
    public void setStatus(Integer status) {
        this.status = status;
    }


    public Long getThreadId() {
        return this.threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getSenderId() {
        return this.senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Boolean getRead() {
        return this.read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 304244433)
    public User getSender() {
        Long __key = this.senderId;
        if (Sender__resolvedKey == null || !Sender__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User SenderNew = targetDao.load(__key);
            synchronized (this) {
                Sender = SenderNew;
                Sender__resolvedKey = __key;
            }
        }
        return Sender;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1509869518)
    public void setSender(User Sender) {
        synchronized (this) {
            this.Sender = Sender;
            senderId = Sender == null ? null : Sender.getId();
            Sender__resolvedKey = senderId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1483947909)
    public Thread getThread() {
        Long __key = this.threadId;
        if (thread__resolvedKey == null || !thread__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreadDao targetDao = daoSession.getThreadDao();
            Thread threadNew = targetDao.load(__key);
            synchronized (this) {
                thread = threadNew;
                thread__resolvedKey = __key;
            }
        }
        return thread;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1938921797)
    public void setThread(Thread thread) {
        synchronized (this) {
            this.thread = thread;
            threadId = thread == null ? null : thread.getId();
            thread__resolvedKey = threadId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 747015224)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMessageDao() : null;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

}