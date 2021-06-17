package com.edusoho.kuozhi.clean.bean.mystudy;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by RexXiang on 2018/2/6.
 */

public class ExamInfoModel implements Serializable{


    /**
     * id : 948
     * name : 管理员测试
     * type : open
     * status : published
     * endTime : 2021-06-18T16:55:00+08:00
     * length : 0
     * passScore : 0.0
     * resitTimes : 0
     * file : {"id":"8833","globalId":"98d12e95194e41349f85ea6a6e4839ff","status":"uploaded","hashId":"course-activity-1067/20210608015608-tf4mz93aczkw8wgk","targetId":"1067","targetType":"course-activity","useType":null,"filename":"4099dfe028fe82f6c63d41098edfd86a.mp4","ext":"mp4","fileSize":"19845311","etag":"course-activity-1067/20210608015608-tf4mz93aczkw8wgk","length":"685","description":"","convertHash":"course-activity-1067/20210608015608-tf4mz93aczkw8wgk","convertStatus":"success","convertParams":{"convertor":"HLSEncryptedVideo","videoQuality":"high","audioQuality":"high"},"metas":{"report":null,"output":"video","size":0,"length":0,"levels":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}},"hd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8","hlsKey":"7f3919fad5f340eb","type":"hd","cmd":{"hlsKey":"7f3919fad5f340eb"}}},"audiolevels":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb"}}},"metas2":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}},"hd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8","hlsKey":"7f3919fad5f340eb","type":"hd","cmd":{"hlsKey":"7f3919fad5f340eb"}}},"type":"video","storage":"cloud","isPublic":"0","canDownload":"0","usedCount":"1","updatedUserId":"2","updatedTime":"0","createdUserId":"2","createdTime":"1623131611","audioConvertStatus":"success","userId":"1","no":"98d12e95194e41349f85ea6a6e4839ff","extno":"8833","hash":"cmd5|04a263cb338cc74d414801330227c0e9","name":"4099dfe028fe82f6c63d41098edfd86a.mp4","resType":"normal","size":"19845311","quality":"","thumbnail":null,"bucket":"tsg-private","private":"1","reskey":"course-activity-1067/20210608015608-tf4mz93aczkw8wgk","tags":null,"views":"0","endShared":"0","endUser":"","directives":{"videoQuality":"high","audioQuality":"high","supportMobile":"0","output":"video"},"processNo":"75c86c23c8f64a0f95c321a43e4a7208","processStatus":"ok","processAudioStatus":"ok","processRetry":"0","mcStatus":"no","processProgress":"0","isRetryFusion":"0","processedTime":"1623131815","deletedTime":"0","audioMetas":{"levels":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}}},"audioMetas2":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}},"mediaUri":"https://elearning.jtport.com/hls/8833/playlist/Xa7xXROilKq5HBBcUb23TJzDzusssPVr.m3u8?format=json&line="}
     * questionsCount : 50
     * score : 100.0
     * remainingResitTimes : 0
     */

    private String id;
    private String name;
    private String type;
    private String status;
    private String endTime;
    private String length;
    private String passScore;
    private String resitTimes;
    private FileBean file;
    private String questionsCount;
    private String score;
    private String remainingResitTimes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }

    public String getResitTimes() {
        return resitTimes;
    }

    public void setResitTimes(String resitTimes) {
        this.resitTimes = resitTimes;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public String getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(String questionsCount) {
        this.questionsCount = questionsCount;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getRemainingResitTimes() {
        return remainingResitTimes;
    }

    public void setRemainingResitTimes(String remainingResitTimes) {
        this.remainingResitTimes = remainingResitTimes;
    }

    public static class FileBean {
        /**
         * id : 8833
         * globalId : 98d12e95194e41349f85ea6a6e4839ff
         * status : uploaded
         * hashId : course-activity-1067/20210608015608-tf4mz93aczkw8wgk
         * targetId : 1067
         * targetType : course-activity
         * useType : null
         * filename : 4099dfe028fe82f6c63d41098edfd86a.mp4
         * ext : mp4
         * fileSize : 19845311
         * etag : course-activity-1067/20210608015608-tf4mz93aczkw8wgk
         * length : 685
         * description :
         * convertHash : course-activity-1067/20210608015608-tf4mz93aczkw8wgk
         * convertStatus : success
         * convertParams : {"convertor":"HLSEncryptedVideo","videoQuality":"high","audioQuality":"high"}
         * metas : {"report":null,"output":"video","size":0,"length":0,"levels":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}},"hd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8","hlsKey":"7f3919fad5f340eb","type":"hd","cmd":{"hlsKey":"7f3919fad5f340eb"}}},"audiolevels":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb"}}}
         * metas2 : {"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}},"hd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8","hlsKey":"7f3919fad5f340eb","type":"hd","cmd":{"hlsKey":"7f3919fad5f340eb"}}}
         * type : video
         * storage : cloud
         * isPublic : 0
         * canDownload : 0
         * usedCount : 1
         * updatedUserId : 2
         * updatedTime : 0
         * createdUserId : 2
         * createdTime : 1623131611
         * audioConvertStatus : success
         * userId : 1
         * no : 98d12e95194e41349f85ea6a6e4839ff
         * extno : 8833
         * hash : cmd5|04a263cb338cc74d414801330227c0e9
         * name : 4099dfe028fe82f6c63d41098edfd86a.mp4
         * resType : normal
         * size : 19845311
         * quality :
         * thumbnail : null
         * bucket : tsg-private
         * private : 1
         * reskey : course-activity-1067/20210608015608-tf4mz93aczkw8wgk
         * tags : null
         * views : 0
         * endShared : 0
         * endUser :
         * directives : {"videoQuality":"high","audioQuality":"high","supportMobile":"0","output":"video"}
         * processNo : 75c86c23c8f64a0f95c321a43e4a7208
         * processStatus : ok
         * processAudioStatus : ok
         * processRetry : 0
         * mcStatus : no
         * processProgress : 0
         * isRetryFusion : 0
         * processedTime : 1623131815
         * deletedTime : 0
         * audioMetas : {"levels":{"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}}}
         * audioMetas2 : {"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}}
         * mediaUri : https://elearning.jtport.com/hls/8833/playlist/Xa7xXROilKq5HBBcUb23TJzDzusssPVr.m3u8?format=json&line=
         */

        private String id;
        private String globalId;
        private String status;
        private String hashId;
        private String targetId;
        private String targetType;
        private Object useType;
        private String filename;
        private String ext;
        private String fileSize;
        private String etag;
        private String length;
        private String description;
        private String convertHash;
        private String convertStatus;
        private ConvertParamsBean convertParams;
        private MetasBean metas;
        private Metas2Bean metas2;
        private String type;
        private String storage;
        private String isPublic;
        private String canDownload;
        private String usedCount;
        private String updatedUserId;
        private String updatedTime;
        private String createdUserId;
        private String createdTime;
        private String audioConvertStatus;
        private String userId;
        private String no;
        private String extno;
        private String hash;
        private String name;
        private String resType;
        private String size;
        private String quality;
        private Object thumbnail;
        private String bucket;
        @SerializedName("private")
        private String privateX;
        private String reskey;
        private Object tags;
        private String views;
        private String endShared;
        private String endUser;
        private DirectivesBean directives;
        private String processNo;
        private String processStatus;
        private String processAudioStatus;
        private String processRetry;
        private String mcStatus;
        private String processProgress;
        private String isRetryFusion;
        private String processedTime;
        private String deletedTime;
        private AudioMetasBean audioMetas;
        private AudioMetas2Bean audioMetas2;
        private String mediaUri;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGlobalId() {
            return globalId;
        }

        public void setGlobalId(String globalId) {
            this.globalId = globalId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getHashId() {
            return hashId;
        }

        public void setHashId(String hashId) {
            this.hashId = hashId;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public Object getUseType() {
            return useType;
        }

        public void setUseType(Object useType) {
            this.useType = useType;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getConvertHash() {
            return convertHash;
        }

        public void setConvertHash(String convertHash) {
            this.convertHash = convertHash;
        }

        public String getConvertStatus() {
            return convertStatus;
        }

        public void setConvertStatus(String convertStatus) {
            this.convertStatus = convertStatus;
        }

        public ConvertParamsBean getConvertParams() {
            return convertParams;
        }

        public void setConvertParams(ConvertParamsBean convertParams) {
            this.convertParams = convertParams;
        }

        public MetasBean getMetas() {
            return metas;
        }

        public void setMetas(MetasBean metas) {
            this.metas = metas;
        }

        public Metas2Bean getMetas2() {
            return metas2;
        }

        public void setMetas2(Metas2Bean metas2) {
            this.metas2 = metas2;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStorage() {
            return storage;
        }

        public void setStorage(String storage) {
            this.storage = storage;
        }

        public String getIsPublic() {
            return isPublic;
        }

        public void setIsPublic(String isPublic) {
            this.isPublic = isPublic;
        }

        public String getCanDownload() {
            return canDownload;
        }

        public void setCanDownload(String canDownload) {
            this.canDownload = canDownload;
        }

        public String getUsedCount() {
            return usedCount;
        }

        public void setUsedCount(String usedCount) {
            this.usedCount = usedCount;
        }

        public String getUpdatedUserId() {
            return updatedUserId;
        }

        public void setUpdatedUserId(String updatedUserId) {
            this.updatedUserId = updatedUserId;
        }

        public String getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }

        public String getCreatedUserId() {
            return createdUserId;
        }

        public void setCreatedUserId(String createdUserId) {
            this.createdUserId = createdUserId;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getAudioConvertStatus() {
            return audioConvertStatus;
        }

        public void setAudioConvertStatus(String audioConvertStatus) {
            this.audioConvertStatus = audioConvertStatus;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getExtno() {
            return extno;
        }

        public void setExtno(String extno) {
            this.extno = extno;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getResType() {
            return resType;
        }

        public void setResType(String resType) {
            this.resType = resType;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public Object getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Object thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getPrivateX() {
            return privateX;
        }

        public void setPrivateX(String privateX) {
            this.privateX = privateX;
        }

        public String getReskey() {
            return reskey;
        }

        public void setReskey(String reskey) {
            this.reskey = reskey;
        }

        public Object getTags() {
            return tags;
        }

        public void setTags(Object tags) {
            this.tags = tags;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public String getEndShared() {
            return endShared;
        }

        public void setEndShared(String endShared) {
            this.endShared = endShared;
        }

        public String getEndUser() {
            return endUser;
        }

        public void setEndUser(String endUser) {
            this.endUser = endUser;
        }

        public DirectivesBean getDirectives() {
            return directives;
        }

        public void setDirectives(DirectivesBean directives) {
            this.directives = directives;
        }

        public String getProcessNo() {
            return processNo;
        }

        public void setProcessNo(String processNo) {
            this.processNo = processNo;
        }

        public String getProcessStatus() {
            return processStatus;
        }

        public void setProcessStatus(String processStatus) {
            this.processStatus = processStatus;
        }

        public String getProcessAudioStatus() {
            return processAudioStatus;
        }

        public void setProcessAudioStatus(String processAudioStatus) {
            this.processAudioStatus = processAudioStatus;
        }

        public String getProcessRetry() {
            return processRetry;
        }

        public void setProcessRetry(String processRetry) {
            this.processRetry = processRetry;
        }

        public String getMcStatus() {
            return mcStatus;
        }

        public void setMcStatus(String mcStatus) {
            this.mcStatus = mcStatus;
        }

        public String getProcessProgress() {
            return processProgress;
        }

        public void setProcessProgress(String processProgress) {
            this.processProgress = processProgress;
        }

        public String getIsRetryFusion() {
            return isRetryFusion;
        }

        public void setIsRetryFusion(String isRetryFusion) {
            this.isRetryFusion = isRetryFusion;
        }

        public String getProcessedTime() {
            return processedTime;
        }

        public void setProcessedTime(String processedTime) {
            this.processedTime = processedTime;
        }

        public String getDeletedTime() {
            return deletedTime;
        }

        public void setDeletedTime(String deletedTime) {
            this.deletedTime = deletedTime;
        }

        public AudioMetasBean getAudioMetas() {
            return audioMetas;
        }

        public void setAudioMetas(AudioMetasBean audioMetas) {
            this.audioMetas = audioMetas;
        }

        public AudioMetas2Bean getAudioMetas2() {
            return audioMetas2;
        }

        public void setAudioMetas2(AudioMetas2Bean audioMetas2) {
            this.audioMetas2 = audioMetas2;
        }

        public String getMediaUri() {
            return mediaUri;
        }

        public void setMediaUri(String mediaUri) {
            this.mediaUri = mediaUri;
        }

        public static class ConvertParamsBean {
            /**
             * convertor : HLSEncryptedVideo
             * videoQuality : high
             * audioQuality : high
             */

            private String convertor;
            private String videoQuality;
            private String audioQuality;

            public String getConvertor() {
                return convertor;
            }

            public void setConvertor(String convertor) {
                this.convertor = convertor;
            }

            public String getVideoQuality() {
                return videoQuality;
            }

            public void setVideoQuality(String videoQuality) {
                this.videoQuality = videoQuality;
            }

            public String getAudioQuality() {
                return audioQuality;
            }

            public void setAudioQuality(String audioQuality) {
                this.audioQuality = audioQuality;
            }
        }

        public static class MetasBean {
            /**
             * report : null
             * output : video
             * size : 0
             * length : 0
             * levels : {"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}},"hd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8","hlsKey":"7f3919fad5f340eb","type":"hd","cmd":{"hlsKey":"7f3919fad5f340eb"}}}
             * audiolevels : {"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb"}}
             */

            private Object report;
            private String output;
            private int size;
            private int length;
            private LevelsBean levels;
            private AudiolevelsBean audiolevels;

            public Object getReport() {
                return report;
            }

            public void setReport(Object report) {
                this.report = report;
            }

            public String getOutput() {
                return output;
            }

            public void setOutput(String output) {
                this.output = output;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getLength() {
                return length;
            }

            public void setLength(int length) {
                this.length = length;
            }

            public LevelsBean getLevels() {
                return levels;
            }

            public void setLevels(LevelsBean levels) {
                this.levels = levels;
            }

            public AudiolevelsBean getAudiolevels() {
                return audiolevels;
            }

            public void setAudiolevels(AudiolevelsBean audiolevels) {
                this.audiolevels = audiolevels;
            }

            public static class LevelsBean {
                /**
                 * sd : {"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}
                 * hd : {"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8","hlsKey":"7f3919fad5f340eb","type":"hd","cmd":{"hlsKey":"7f3919fad5f340eb"}}
                 */

                private SdBean sd;
                private HdBean hd;

                public SdBean getSd() {
                    return sd;
                }

                public void setSd(SdBean sd) {
                    this.sd = sd;
                }

                public HdBean getHd() {
                    return hd;
                }

                public void setHd(HdBean hd) {
                    this.hd = hd;
                }

                public static class SdBean {
                    /**
                     * key : 1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8
                     * hlsKey : 7f3919fad5f340eb
                     * type : sd
                     * cmd : {"hlsKey":"7f3919fad5f340eb"}
                     */

                    private String key;
                    private String hlsKey;
                    private String type;
                    private CmdBean cmd;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getHlsKey() {
                        return hlsKey;
                    }

                    public void setHlsKey(String hlsKey) {
                        this.hlsKey = hlsKey;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public CmdBean getCmd() {
                        return cmd;
                    }

                    public void setCmd(CmdBean cmd) {
                        this.cmd = cmd;
                    }

                    public static class CmdBean {
                        /**
                         * hlsKey : 7f3919fad5f340eb
                         */

                        private String hlsKey;

                        public String getHlsKey() {
                            return hlsKey;
                        }

                        public void setHlsKey(String hlsKey) {
                            this.hlsKey = hlsKey;
                        }
                    }
                }

                public static class HdBean {
                    /**
                     * key : 1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8
                     * hlsKey : 7f3919fad5f340eb
                     * type : hd
                     * cmd : {"hlsKey":"7f3919fad5f340eb"}
                     */

                    private String key;
                    private String hlsKey;
                    private String type;
                    private CmdBeanX cmd;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getHlsKey() {
                        return hlsKey;
                    }

                    public void setHlsKey(String hlsKey) {
                        this.hlsKey = hlsKey;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public CmdBeanX getCmd() {
                        return cmd;
                    }

                    public void setCmd(CmdBeanX cmd) {
                        this.cmd = cmd;
                    }

                    public static class CmdBeanX {
                        /**
                         * hlsKey : 7f3919fad5f340eb
                         */

                        private String hlsKey;

                        public String getHlsKey() {
                            return hlsKey;
                        }

                        public void setHlsKey(String hlsKey) {
                            this.hlsKey = hlsKey;
                        }
                    }
                }
            }

            public static class AudiolevelsBean {
                /**
                 * sd : {"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb"}
                 */

                private SdBeanX sd;

                public SdBeanX getSd() {
                    return sd;
                }

                public void setSd(SdBeanX sd) {
                    this.sd = sd;
                }

                public static class SdBeanX {
                    /**
                     * key : 1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8
                     * hlsKey : 7f3919fad5f340eb
                     */

                    private String key;
                    private String hlsKey;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getHlsKey() {
                        return hlsKey;
                    }

                    public void setHlsKey(String hlsKey) {
                        this.hlsKey = hlsKey;
                    }
                }
            }
        }

        public static class Metas2Bean {
            /**
             * sd : {"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}
             * hd : {"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8","hlsKey":"7f3919fad5f340eb","type":"hd","cmd":{"hlsKey":"7f3919fad5f340eb"}}
             */

            private SdBeanXX sd;
            private HdBeanX hd;

            public SdBeanXX getSd() {
                return sd;
            }

            public void setSd(SdBeanXX sd) {
                this.sd = sd;
            }

            public HdBeanX getHd() {
                return hd;
            }

            public void setHd(HdBeanX hd) {
                this.hd = hd;
            }

            public static class SdBeanXX {
                /**
                 * key : 1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd.m3u8
                 * hlsKey : 7f3919fad5f340eb
                 * type : sd
                 * cmd : {"hlsKey":"7f3919fad5f340eb"}
                 */

                private String key;
                private String hlsKey;
                private String type;
                private CmdBeanXX cmd;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getHlsKey() {
                    return hlsKey;
                }

                public void setHlsKey(String hlsKey) {
                    this.hlsKey = hlsKey;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public CmdBeanXX getCmd() {
                    return cmd;
                }

                public void setCmd(CmdBeanXX cmd) {
                    this.cmd = cmd;
                }

                public static class CmdBeanXX {
                    /**
                     * hlsKey : 7f3919fad5f340eb
                     */

                    private String hlsKey;

                    public String getHlsKey() {
                        return hlsKey;
                    }

                    public void setHlsKey(String hlsKey) {
                        this.hlsKey = hlsKey;
                    }
                }
            }

            public static class HdBeanX {
                /**
                 * key : 1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-hd.m3u8
                 * hlsKey : 7f3919fad5f340eb
                 * type : hd
                 * cmd : {"hlsKey":"7f3919fad5f340eb"}
                 */

                private String key;
                private String hlsKey;
                private String type;
                private CmdBeanXXX cmd;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getHlsKey() {
                    return hlsKey;
                }

                public void setHlsKey(String hlsKey) {
                    this.hlsKey = hlsKey;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public CmdBeanXXX getCmd() {
                    return cmd;
                }

                public void setCmd(CmdBeanXXX cmd) {
                    this.cmd = cmd;
                }

                public static class CmdBeanXXX {
                    /**
                     * hlsKey : 7f3919fad5f340eb
                     */

                    private String hlsKey;

                    public String getHlsKey() {
                        return hlsKey;
                    }

                    public void setHlsKey(String hlsKey) {
                        this.hlsKey = hlsKey;
                    }
                }
            }
        }

        public static class DirectivesBean {
            /**
             * videoQuality : high
             * audioQuality : high
             * supportMobile : 0
             * output : video
             */

            private String videoQuality;
            private String audioQuality;
            private String supportMobile;
            private String output;

            public String getVideoQuality() {
                return videoQuality;
            }

            public void setVideoQuality(String videoQuality) {
                this.videoQuality = videoQuality;
            }

            public String getAudioQuality() {
                return audioQuality;
            }

            public void setAudioQuality(String audioQuality) {
                this.audioQuality = audioQuality;
            }

            public String getSupportMobile() {
                return supportMobile;
            }

            public void setSupportMobile(String supportMobile) {
                this.supportMobile = supportMobile;
            }

            public String getOutput() {
                return output;
            }

            public void setOutput(String output) {
                this.output = output;
            }
        }

        public static class AudioMetasBean {
            /**
             * levels : {"sd":{"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}}
             */

            private LevelsBeanX levels;

            public LevelsBeanX getLevels() {
                return levels;
            }

            public void setLevels(LevelsBeanX levels) {
                this.levels = levels;
            }

            public static class LevelsBeanX {
                /**
                 * sd : {"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}
                 */

                private SdBeanXXX sd;

                public SdBeanXXX getSd() {
                    return sd;
                }

                public void setSd(SdBeanXXX sd) {
                    this.sd = sd;
                }

                public static class SdBeanXXX {
                    /**
                     * key : 1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8
                     * hlsKey : 7f3919fad5f340eb
                     * type : sd
                     * cmd : {"hlsKey":"7f3919fad5f340eb"}
                     */

                    private String key;
                    private String hlsKey;
                    private String type;
                    private CmdBeanXXXX cmd;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getHlsKey() {
                        return hlsKey;
                    }

                    public void setHlsKey(String hlsKey) {
                        this.hlsKey = hlsKey;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public CmdBeanXXXX getCmd() {
                        return cmd;
                    }

                    public void setCmd(CmdBeanXXXX cmd) {
                        this.cmd = cmd;
                    }

                    public static class CmdBeanXXXX {
                        /**
                         * hlsKey : 7f3919fad5f340eb
                         */

                        private String hlsKey;

                        public String getHlsKey() {
                            return hlsKey;
                        }

                        public void setHlsKey(String hlsKey) {
                            this.hlsKey = hlsKey;
                        }
                    }
                }
            }
        }

        public static class AudioMetas2Bean {
            /**
             * sd : {"key":"1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8","hlsKey":"7f3919fad5f340eb","type":"sd","cmd":{"hlsKey":"7f3919fad5f340eb"}}
             */

            private SdBeanXXXX sd;

            public SdBeanXXXX getSd() {
                return sd;
            }

            public void setSd(SdBeanXXXX sd) {
                this.sd = sd;
            }

            public static class SdBeanXXXX {
                /**
                 * key : 1/98d12e95194e41349f85ea6a6e4839ff/ljjXsDt6mDVefTo3-ljjXsDt6mDVefTo3-sd-aac.m3u8
                 * hlsKey : 7f3919fad5f340eb
                 * type : sd
                 * cmd : {"hlsKey":"7f3919fad5f340eb"}
                 */

                private String key;
                private String hlsKey;
                private String type;
                private CmdBeanXXXXX cmd;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getHlsKey() {
                    return hlsKey;
                }

                public void setHlsKey(String hlsKey) {
                    this.hlsKey = hlsKey;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public CmdBeanXXXXX getCmd() {
                    return cmd;
                }

                public void setCmd(CmdBeanXXXXX cmd) {
                    this.cmd = cmd;
                }

                public static class CmdBeanXXXXX {
                    /**
                     * hlsKey : 7f3919fad5f340eb
                     */

                    private String hlsKey;

                    public String getHlsKey() {
                        return hlsKey;
                    }

                    public void setHlsKey(String hlsKey) {
                        this.hlsKey = hlsKey;
                    }
                }
            }
        }
    }
}
