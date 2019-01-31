package co.chatsdk.core.dao;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.chatsdk.core.interfaces.CoreEntity;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.StorageManager;
import co.chatsdk.core.utils.StringChecker;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS
// KEEP INCLUDES - put your custom includes here

@org.greenrobot.greendao.annotation.Entity
public class Thread implements CoreEntity {

    @org.greenrobot.greendao.annotation.Id
    private Long id;
    @Unique
    private String entityID;
    private Date creationDate;
    private Boolean hasUnreadMessages;
    private Boolean deleted;
    private String name;
    private Integer type;
    private String creatorEntityId;
    private String imageUrl;
    private String rootKey;
    private String apiKey; // TODO: Delete this
    private Long creatorId;
    private Long lastMessageId;

    @ToOne(joinProperty = "lastMessageId")
    private Message lastMessage;

    @ToOne(joinProperty = "creatorId")
    private User creator;

    @ToMany
    @JoinEntity(
            entity = UserThreadLink.class,
            sourceProperty = "threadId",
            targetProperty = "userId"
    )
    private List<UserThreadLink> userThreadLinks;

    @ToMany(referencedJoinProperty = "threadId")
    private List<ThreadMetaValue> metaValues;

    @ToMany(referencedJoinProperty = "threadId")
    @OrderBy("date ASC")
    private List<Message> messages;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 473811190)
    private transient ThreadDao myDao;
    @Generated(hash = 1767171241)
    private transient Long creator__resolvedKey;
    @Generated(hash = 88977546)
    private transient Long lastMessage__resolvedKey;

    public Thread() {
    }

    public Thread(Long id) {
        this.id = id;
    }

    @Generated(hash = 859547806)
    public Thread(Long id, String entityID, Date creationDate, Boolean hasUnreadMessages, Boolean deleted, String name, Integer type,
            String creatorEntityId, String imageUrl, String rootKey, String apiKey, Long creatorId, Long lastMessageId) {
        this.id = id;
        this.entityID = entityID;
        this.creationDate = creationDate;
        this.hasUnreadMessages = hasUnreadMessages;
        this.deleted = deleted;
        this.name = name;
        this.type = type;
        this.creatorEntityId = creatorEntityId;
        this.imageUrl = imageUrl;
        this.rootKey = rootKey;
        this.apiKey = apiKey;
        this.creatorId = creatorId;
        this.lastMessageId = lastMessageId;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getUsers(){
        /* Getting the users list by getUserThreadLink can be out of date so we get the data from the database*/

        List<UserThreadLink> list =  DaoCore.fetchEntitiesWithProperty(UserThreadLink.class, UserThreadLinkDao.Properties.ThreadId, getId());

        //if (DEBUG) Timber.d("Thread, getUsers, Amount: %s", (list == null ? "null" : list.size()));

        List<User> users  = new ArrayList<User>();

        if (list == null) {
            return users;
        }

        for (UserThreadLink data : list)
            if (data.getUser() != null && !users.contains(data.getUser()))
                users.add(data.getUser());

        return users;
    }

    public boolean containsUser (User user) {
        for(User u : getUsers()) {
            if (u.getEntityID().equals(user.getEntityID())) {
                return true;
            }
        }
        return false;
    }

    public Date lastMessageAddedDate (){
        Date date = creationDate;

        List<Message> list = getMessagesWithOrder(DaoCore.ORDER_DESC);

        if (list.size() > 0) {
            date = list.get(0).getDate().toDate();
        }

        if (date == null) {
            date = new Date();
        }

        return date;
    }

    public void addUser (User user) {
        DaoCore.connectUserAndThread(user, this);
        this.update();
        user.update();
    }

    public void removeUser (User user) {
        DaoCore.breakUserAndThread(user, this);
        this.update();
        user.update();
    }

    public User otherUser () {
        if (getUsers().size() == 2) {
            for (User u : getUsers()) {
                if (!u.isMe()) {
                    return u;
                }
            }
        }
        return null;
    }

    public void addUsers (User... users) {
        addUsers(Arrays.asList(users));
    }

    public void addUsers (List<User> users) {
        for(User u : users) {
            addUser(u);
        }
    }

    public void removeUsers (List<User> users) {
        for(User u : users) {
            removeUser(u);
        }
    }



    public boolean containsMessageWithID (String messageEntityID) {
        for(Message m : getMessages()) {
            if(m.getEntityID() != null && messageEntityID != null && m.getEntityID().equals(messageEntityID)) {
                return true;
            }
        }
        return false;
    }

    public void removeUsers (User... users) {
        removeUsers(Arrays.asList(users));
    }

    /** Fetch messages list from the db for current thread, Messages will be order Desc/Asc on demand.*/
    public List<Message> getMessagesWithOrder(int order){
        return getMessagesWithOrder(order, -1);
    }

    /** Fetch messages list from the db for current thread, Messages will be order Desc/Asc on demand.*/
    @Keep
    public List<Message> getMessagesWithOrder(int order, int limit) {
        QueryBuilder<Message> qb = daoSession.queryBuilder(Message.class);
        qb.where(MessageDao.Properties.ThreadId.eq(getId()));

        if(order == DaoCore.ORDER_ASC) {
            qb.orderAsc(MessageDao.Properties.Date);
        }
        else if(order == DaoCore.ORDER_DESC) {
            qb.orderDesc(MessageDao.Properties.Date);
        }

        // Making sure no null messages infected the sort.
        qb.where(MessageDao.Properties.Date.isNotNull());

        if (limit > 0) {
            qb.limit(limit);
        }

        Query<Message> query = qb.build().forCurrentThread();
        return query.list();
    }

    public void addMessage (Message message) {
        setLastMessage(message);
        message.setThreadId(this.getId());
        message.update();
        getMessages().add(message);
        update();
//        resetMessages();
    }

    /**
     * Setting the metaData, The Map will be converted to a Json String.
     **/
    public void setMetaMap(Map<String, String> metadata){
        for(String key : metadata.keySet()) {
            setMetaValue(key, metadata.get(key));
        }
    }

    /**
     * Converting the metaData json to a map object
     **/
    public Map<String, String> metaMap() {
        HashMap<String, String> map = new HashMap<>();

        for (ThreadMetaValue v : getMetaValues()) {
            map.put(v.getKey(), v.getValue());
        }

        return map;
    }

    @Keep
    public void setMetaValue (String key, String value) {
        ThreadMetaValue metaValue = metaValueForKey(key);
        if (metaValue == null) {
            metaValue = ChatSDK.db().createEntity(ThreadMetaValue.class);
            metaValue.setThreadId(this.getId());
            getMetaValues().add(metaValue);
        }
        metaValue.setValue(value);
        metaValue.setKey(key);
        metaValue.update();
        update();
    }

    @Keep
    public void updateValues (HashMap<String, String> values) {
        for (String key : values.keySet()) {
            setMetaValue(key, values.get(key));
        }
    }

    @Keep
    public ThreadMetaValue metaValueForKey (String key) {
        ArrayList<MetaValue> values = new ArrayList<>();
        values.addAll(getMetaValues());
        return (ThreadMetaValue) MetaValueHelper.metaValueForKey(key, values);
    }

    public String metaStringForKey(String key) {
        return metaMap().get(key);
    }

    public void setMetaString(String key, String value) {
        setMetaValue(key, value);
        update();
    }

    public void removeMessage (Message message) {

        List<Message> messages = getMessages();

        if (messages.contains(message)) {
            messages.remove(message);
        }

        message.cascadeDelete();

        update();
        resetMessages();

        if (messages.size() > 1) {
            setLastMessage(messages.get(messages.size()-1));
        }
        else {
            //
            setLastMessageId(Long.valueOf(0));
            update();
        }
    }

    public boolean hasUser(User user) {

        UserThreadLink data =
                DaoCore.fetchEntityWithProperties(UserThreadLink.class,
                        new Property[]{UserThreadLinkDao.Properties.ThreadId, UserThreadLinkDao.Properties.UserId}, getId(), user.getId());

        return data != null;
    }

    public int getUnreadMessagesCount() {
        int count = 0;
        List<Message> messages = getMessagesWithOrder(DaoCore.ORDER_DESC);
        for (Message m : messages)
        {
            if(!m.isRead())
                count++;
            else break;
        }

        return count;
    }

    public boolean isLastMessageWasRead(){
        List<Message> messages = getMessagesWithOrder(DaoCore.ORDER_DESC);
        return messages == null || messages.size() == 0 || messages.get(0).isRead();
    }

    public boolean isDeleted(){
        return deleted != null && deleted;
    }

    public void markRead () {
        ArrayList<Message> messages = new ArrayList<>(getMessages());
        for(Message m : messages) {
            m.setRead(true);
            m.update();
        }
        update();
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

    public java.util.Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(java.util.Date creationDate) {
        this.creationDate = creationDate;
    }

    // TODO: Unused remove this
    @Deprecated
    public Boolean getHasUnreadMessages() {
        return this.hasUnreadMessages;
    }

    @Deprecated
    public void setHasUnreadMessages(Boolean hasUnreadMessages) {
        this.hasUnreadMessages = hasUnreadMessages;
    }

    public Boolean getDeleted() {
        return deleted != null ? deleted : false;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName () {
        // Either get the name or return the names of the participants
        if(!StringChecker.isNullOrEmpty(getName())) {
            return getName();
        }
        else {
            return getUserListString();
        }
    }

    public String getUserListString () {
        String name = "";
        for(User u : getUsers()) {
            if(!u.isMe() && !StringUtils.isEmpty(u.getName())) {
                name += u.getName() + ", ";
            }
        }
        if(name.length() >= 2) {
            name = name.substring(0, name.length() - 2);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastMessageAddedDate () {
        Message lastMessage = lastMessage();
        if(lastMessage != null && lastMessage.getDate() != null) {
            return lastMessage.getDate().toDate();
        }
        return null;
    }

    public Integer getType() {
        return this.type;
    }

    public boolean typeIs(int value) {
        return getType() != null && (getType() & value) > 0;
    }

    public boolean typeOr (int value) {
        return getType() != null && (getType() | value) > 0;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCreatorEntityId() {
        return this.creatorEntityId;
    }

    public void setCreatorEntityId(String creatorEntityId) {
        this.creatorEntityId = creatorEntityId;
    }

    public String getRootKey() {
        return this.rootKey;
    }

    public void setRootKey(String rootKey) {
        this.rootKey = rootKey;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Message lastMessage () {
        if(lastMessage == null) {
            List<Message> messages = getMessagesWithOrder(DaoCore.ORDER_DESC);
            if (messages.size() > 0) {
                lastMessage = messages.get(0);
            }
        }
        return lastMessage;
    }

    public Long getCreatorId() {
        return this.creatorId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2088804448)
    public User getCreator() {
        Long __key = this.creatorId;
        if (creator__resolvedKey == null || !creator__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User creatorNew = targetDao.load(__key);
            synchronized (this) {
                creator = creatorNew;
                creator__resolvedKey = __key;
            }
        }
        return creator;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 501133931)
    public void setCreator(User creator) {
        synchronized (this) {
            this.creator = creator;
            creatorId = creator == null ? null : creator.getId();
            creator__resolvedKey = creatorId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 846992604)
    public List<UserThreadLink> getUserThreadLinks() {
        if (userThreadLinks == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserThreadLinkDao targetDao = daoSession.getUserThreadLinkDao();
            List<UserThreadLink> userThreadLinksNew = targetDao._queryThread_UserThreadLinks(id);
            synchronized (this) {
                if (userThreadLinks == null) {
                    userThreadLinks = userThreadLinksNew;
                }
            }
        }
        return userThreadLinks;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 180413695)
    public synchronized void resetUserThreadLinks() {
        userThreadLinks = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 581641122)
    public List<Message> getMessages() {
        if (messages == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MessageDao targetDao = daoSession.getMessageDao();
            List<Message> messagesNew = targetDao._queryThread_Messages(id);
            synchronized (this) {
                if (messages == null) {
                    messages = messagesNew;
                }
            }
        }
        return messages;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1942469556)
    public synchronized void resetMessages() {
        messages = null;
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
    @Generated(hash = 5320433)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getThreadDao() : null;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getLastMessageId() {
        return this.lastMessageId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1697405005)
    public Message getLastMessage() {
        Long __key = this.lastMessageId;
        if (lastMessage__resolvedKey == null || !lastMessage__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MessageDao targetDao = daoSession.getMessageDao();
            Message lastMessageNew = targetDao.load(__key);
            synchronized (this) {
                lastMessage = lastMessageNew;
                lastMessage__resolvedKey = __key;
            }
        }
        return lastMessage;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 944284900)
    public void setLastMessage(Message lastMessage) {
        synchronized (this) {
            this.lastMessage = lastMessage;
            lastMessageId = lastMessage == null ? null : lastMessage.getId();
            lastMessage__resolvedKey = lastMessageId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 132417041)
    public List<ThreadMetaValue> getMetaValues() {
        if (metaValues == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreadMetaValueDao targetDao = daoSession.getThreadMetaValueDao();
            List<ThreadMetaValue> metaValuesNew = targetDao._queryThread_MetaValues(id);
            synchronized (this) {
                if (metaValues == null) {
                    metaValues = metaValuesNew;
                }
            }
        }
        return metaValues;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 365870950)
    public synchronized void resetMetaValues() {
        metaValues = null;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

 


}
