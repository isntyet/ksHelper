package com.ks.dblab.kshelper.call.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ks.dblab.kshelper.R;
import com.ks.dblab.kshelper.call.CallActivity;
import com.ks.dblab.kshelper.call.CallItem;

import java.util.ArrayList;

/**
 * Created by jojo on 2016-07-12.
 */
public class PinnedHeaderAdapter extends BaseAdapter implements AbsListView.OnScrollListener, IPinnedHeader, Filterable {

    private ArrayList<CallItem> callItems;

    private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
    private static final char HANGUL_LAST_UNICODE = 55203; // 힣
    private static final char HANGUL_BASE_UNIT = 588;//각자음 마다 가지는 글자수
    private static final char[] INITIAL_SOUND = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SECTION = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;

    private Dialog dialog = null;

    LayoutInflater mLayoutInflater;
    int mCurrentSectionPosition = 0, mNextSectionPostion = 0;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store list view data
    ArrayList<String> mListItems;

    // context object
    Context mContext;

    public PinnedHeaderAdapter(Context context, ArrayList<String> listItems, ArrayList<Integer> listSectionPos) {
        this.mContext = context;
        this.mListItems = listItems;
        this.mListSectionPos = listSectionPos;

        callItems = setCallList();

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mListSectionPos.contains(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mListItems.get(position).hashCode();
    }

    private String getPhoneNum(String name) {
        String phone_num = "";
        for (int i = 0; i < callItems.size(); i++) {
            if (name.equals(callItems.get(i).getName())) {
                phone_num = callItems.get(i).getNumber();
                break;
            }
        }
        return phone_num;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final String item = mListItems.get(position).toString();

        final String phone_num = getPhoneNum(mListItems.get(position).toString());

        if (convertView == null) {
            holder = new ViewHolder();
            int type = getItemViewType(position);

            switch (type) {
                case TYPE_ITEM:
                    convertView = mLayoutInflater.inflate(R.layout.item_indexer_row_view, null);
                    break;
                case TYPE_SECTION:
                    convertView = mLayoutInflater.inflate(R.layout.item_indexer_section_row_view, null);
                    break;
            }
            holder.textView = (TextView) convertView.findViewById(R.id.row_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mListItems.get(position).toString());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog(item, phone_num);
            }
        });


        return convertView;
    }

    private void loadDialog(String name, final String phone_num) {

        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.item_dialog_call);
        dialog.setTitle("전화걸기 확인 중");

        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_content);
        tvContent.setText("(" + name + ") 에 전화 하시겠습니까?");

        dialog.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall(phone_num);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void startCall(String num) {
        Intent it = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mContext.startActivity(it);
    }


    @Override
    public int getPinnedHeaderState(int position) {
        // hide pinned header when items count is zero OR position is less than
        // zero OR
        // there is already a header in list view
        if (getCount() == 0 || position < 0 || mListSectionPos.indexOf(position) != -1) {
            return PINNED_HEADER_GONE;
        }

        // the header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        mNextSectionPostion = getNextSectionPosition(mCurrentSectionPosition);
        if (mNextSectionPostion != -1 && position == mNextSectionPostion - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }

        return PINNED_HEADER_VISIBLE;
    }

    public int getCurrentSectionPosition(int position) {
        //String listChar = mListItems.get(position).toString().substring(0, 1).toUpperCase(Locale.getDefault());
        String listChar = getInitialSound(mListItems.get(position).charAt(0)) + "";
        return mListItems.indexOf(listChar);
    }

    private char getInitialSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return INITIAL_SOUND[index];
    }

    public int getNextSectionPosition(int currentSectionPosition) {
        int index = mListSectionPos.indexOf(currentSectionPosition);
        if ((index + 1) < mListSectionPos.size()) {
            return mListSectionPos.get(index + 1);
        }
        return mListSectionPos.get(index);
    }

    @Override
    public void configurePinnedHeader(View v, int position) {
        // set text in pinned header
        TextView header = (TextView) v;
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        header.setText(mListItems.get(mCurrentSectionPosition));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
    }

    @Override
    public Filter getFilter() {
        return ((CallActivity) mContext).new ListFilter();
    }

    public static class ViewHolder {
        public TextView textView;
    }

    private ArrayList<CallItem> setCallList() {
        ArrayList<CallItem> callDatas = new ArrayList<CallItem>();

        callDatas.add(new CallItem("교목실", "0516634113"));
        callDatas.add(new CallItem("대학본부-처부속실", "0516634116"));
        callDatas.add(new CallItem("대학본부-FAX", "0516634118"));
        callDatas.add(new CallItem("교육연구처-처장", "0516634010"));
        callDatas.add(new CallItem("교육연구처-부처장(학사관리)", "0516634013"));
        callDatas.add(new CallItem("교육연구처-부처장(교원정책)", "0516634011"));
        callDatas.add(new CallItem("학사관리팀-팀장", "0516634020"));
        callDatas.add(new CallItem("학사관리팀-수강신청,성적,계절수업", "0516634021"));
        callDatas.add(new CallItem("학사관리팀-수업", "0516634022"));
        callDatas.add(new CallItem("학사관리팀-강의시수,교육과정", "0516634023"));
        callDatas.add(new CallItem("학사관리팀-종합외래교수실", "0516635957"));
        callDatas.add(new CallItem("학사관리팀-졸업사정,복수(부)전공", "0516634024"));
        callDatas.add(new CallItem("학사관리팀-학적,전과,학력조회", "0516634026"));
        callDatas.add(new CallItem("학사관리팀-학생증(신분증)발급", "0516634191"));
        callDatas.add(new CallItem("학사관리팀-휴·복학", "0516634192"));
        callDatas.add(new CallItem("학사관리팀-학생생애관리", "0516634106"));
        callDatas.add(new CallItem("학사관리팀-FAX", "0516634193"));
        callDatas.add(new CallItem("교원지원팀-팀장 ", "0516634070"));
        callDatas.add(new CallItem("교원지원팀-교원출장, 출/귀국,조교인사,연구소", "0516634071"));
        callDatas.add(new CallItem("교원지원팀-전임교원인사,교원복무,연구년", "0516634072"));
        callDatas.add(new CallItem("교원지원팀-외국인교원인사,교원업적(일반)", "0516634073"));
        callDatas.add(new CallItem("교원지원팀-비전임교원인사,교원업적(산학,강의)", "0516634074"));
        callDatas.add(new CallItem("교원지원팀-FAX", "0516634079"));
        callDatas.add(new CallItem("교직부-부장", "0516634010"));
        callDatas.add(new CallItem("교직부-팀장", "0516634020"));
        callDatas.add(new CallItem("교직부-(대학)교직,실습", "0516634027"));
        callDatas.add(new CallItem("교직부-(교육대학원)교직,실습", "0516635575"));
        callDatas.add(new CallItem("교양지원부-팀장", "0516634020"));
        callDatas.add(new CallItem("교수학습개발센터-소장", "0516635204"));
        callDatas.add(new CallItem("교수학습개발센터-팀장", "0516635440"));
        callDatas.add(new CallItem("교수학습개발센터-e러닝시스템운영", "0516635441"));
        callDatas.add(new CallItem("교수학습개발센터-교수·학습법지원", "0516635442"));
        callDatas.add(new CallItem("교수학습개발센터-28호관1수업 행동분석실 수업지원", "0516635443"));
        callDatas.add(new CallItem("교수학습개발센터-학습법 지원", "0516635444"));
        callDatas.add(new CallItem("교수학습개발센터-학습컨설팅 및 검사", "0516635445"));
        callDatas.add(new CallItem("교수학습개발센터-CT제작 스튜디오", "0516635446"));
        callDatas.add(new CallItem("교수학습개발센터-HD종합편집실", "0516635447"));
        callDatas.add(new CallItem("교수학습개발센터-멀티스튜디오", "0516635448"));
        callDatas.add(new CallItem("교수학습개발센터-27호관 교육매체 지원실", "0516635425"));
        callDatas.add(new CallItem("교수학습개발센터-27호관  교육매체 수업지원", "0516635426"));
        callDatas.add(new CallItem("교수학습개발센터-26호관2수업 행동분석실 수업지원", "0516635427"));
        callDatas.add(new CallItem("교수학습개발센터-멀티미디어소강당", "0516635368"));
        callDatas.add(new CallItem("교수학습개발센터-별관컴퓨터교육지원실", "0516635431"));
        callDatas.add(new CallItem("교수학습개발센터-별관컴퓨터교육실고장신고", "0516635432"));
        callDatas.add(new CallItem("교수학습개발센터-별관디지털출력실", "0516635433"));
        callDatas.add(new CallItem("교수학습개발센터-별관경인쇄실", "0516635434"));
        callDatas.add(new CallItem("교수학습개발센터-26관매체지원실", "0516635435"));
        callDatas.add(new CallItem("교수학습개발센터-28호관 디지털이미지홀 준비실", "0516636530"));
        callDatas.add(new CallItem("교수학습개발센터-29호관 수업지원실", "0516636530"));
        callDatas.add(new CallItem("학생상담센터-소장", "0516634328"));
        callDatas.add(new CallItem("학생상담센터-팀장", "0516634020"));
        callDatas.add(new CallItem("학생상담센터-상담실", "0516635344"));
        callDatas.add(new CallItem("공학교육인증센터-소장", "0516634777"));
        callDatas.add(new CallItem("공학교육인증센터-팀장", "0516634020"));
        callDatas.add(new CallItem("공학교육인증센터-행정실", "0516635484"));
        callDatas.add(new CallItem("학생지원처-처장", "0516634012"));
        callDatas.add(new CallItem("학생지원팀-팀장", "0516634050"));
        callDatas.add(new CallItem("학생지원팀-학생지원(동아리)", "0516634051"));
        callDatas.add(new CallItem("학생지원팀-국가우수,교외장학,대출", "0516634052"));
        callDatas.add(new CallItem("학생지원팀-국가(근로)장학", "0516634053"));
        callDatas.add(new CallItem("학생지원팀-교내장학,근로(국가,교내)", "0516634054"));
        callDatas.add(new CallItem("학생지원팀-국가장학금(1,2유형)", "0516634055"));
        callDatas.add(new CallItem("학생지원팀-FAX", "0516634059"));
        callDatas.add(new CallItem("후생복지팀-팀장", "0516636510"));
        callDatas.add(new CallItem("후생복지팀-사생선발,관리", "0516636511"));
        callDatas.add(new CallItem("후생복지팀-생활관회계", "0516636512"));
        callDatas.add(new CallItem("후생복지팀-식당관리(누리생활관)", "0516636513"));
        callDatas.add(new CallItem("후생복지팀-식당관리(학생회관)", "0516636418"));
        callDatas.add(new CallItem("후생복지팀-식당관리(과학관)", "0516636411"));
        callDatas.add(new CallItem("후생복지팀-사감실(제1누리생활관)", "0516636520"));
        callDatas.add(new CallItem("후생복지팀-사감실(제2누리생활관)", "0516637520"));
        callDatas.add(new CallItem("후생복지팀-FAX", "0516636519"));
        callDatas.add(new CallItem("누리생활관 식당", "0516636516"));
        callDatas.add(new CallItem("학생회관 교직원 식당", "0516636417"));
        callDatas.add(new CallItem("학생회관 학생 식당", "0516636418"));
        callDatas.add(new CallItem("과학관 매점", "0516636415"));
        callDatas.add(new CallItem("학생회관 매점", "0516636416"));
        callDatas.add(new CallItem("누리생활관 매점", "0516218901"));
        callDatas.add(new CallItem("도서관 매점", "01099062046"));
        callDatas.add(new CallItem("문대 매점", "01022727517"));
        callDatas.add(new CallItem("상대 매점", "01047588338"));
        callDatas.add(new CallItem("매장-사진관", "0516237813"));
        callDatas.add(new CallItem("문구점", "0516219772"));
        callDatas.add(new CallItem("안경점", "0516258086"));
        callDatas.add(new CallItem("미용실", "0516276718"));
        callDatas.add(new CallItem("여행사", "0516636526"));
        callDatas.add(new CallItem("기념품점", "0516220926"));
        callDatas.add(new CallItem("핸드폰점", "0516123544"));
        callDatas.add(new CallItem("꽃집", "0516280120"));
        callDatas.add(new CallItem("열쇠,도장", "01038593737"));
        callDatas.add(new CallItem("액서서리점", "0516267848"));
        callDatas.add(new CallItem("이지라이프센터", "0517511481"));
        callDatas.add(new CallItem("복사점(학생회관)", "0516268245"));
        callDatas.add(new CallItem("신라명과", "0516227847"));
        callDatas.add(new CallItem("징코카페(정보관)", "0516636414"));
        callDatas.add(new CallItem("징코카페(도서관)", "0516636412"));
        callDatas.add(new CallItem("부산은행", "0516636527"));
        callDatas.add(new CallItem("우편취급국", "0516636557"));
        callDatas.add(new CallItem("남성신협", "0512073921"));
        callDatas.add(new CallItem("체육부-부장", "0516634050"));
        callDatas.add(new CallItem("체육부-행정실", "0516636531"));
        callDatas.add(new CallItem("체육부-야구부숙소", "0516636532"));
        callDatas.add(new CallItem("체육부-레슬링숙소", "0516636533"));
        callDatas.add(new CallItem("체육부-미식축구부", "0516636534"));
        callDatas.add(new CallItem("체육부-운동장", "0516636535"));
        callDatas.add(new CallItem("체육부-레슬링연습장", "0516636536"));
        callDatas.add(new CallItem("체육부-FAX", "0516634059"));
        callDatas.add(new CallItem("보건진료소-소장 ", "0516634887"));
        callDatas.add(new CallItem("보건진료소-진료실", "0516634127"));
        callDatas.add(new CallItem("하나교육복지센터-소장", "0516634012"));
        callDatas.add(new CallItem("하나교육복지센터-행정실", "0516634055"));
        callDatas.add(new CallItem("기획조정처-처장", "0516634014"));
        callDatas.add(new CallItem("기획조정처-부처장(지표관리)", "0516634015"));
        callDatas.add(new CallItem("기획조정처-부처장(구조개선)", "0516634017"));
        callDatas.add(new CallItem("전략기획팀-팀장", "0516634150"));
        callDatas.add(new CallItem("전략기획팀-전략기획,정원조정", "0516634151"));
        callDatas.add(new CallItem("전략기획팀-대학발전계획,특성화", "0516634152"));
        callDatas.add(new CallItem("전략기획팀-FAX", "0516634189"));
        callDatas.add(new CallItem("예산기획팀-팀장", "0516634153"));
        callDatas.add(new CallItem("예산기획팀-예산(사업비)", "0516634154"));
        callDatas.add(new CallItem("예산기획팀-예산(실험실습비, 출장)", "0516634155"));
        callDatas.add(new CallItem("평가기획팀-팀장", "0516634150"));
        callDatas.add(new CallItem("평가기획팀-정보공시,교육통계,대학평가", "0516634157"));
        callDatas.add(new CallItem("평가기획팀-기타 제반평가,통계연보", "0516634158"));
        callDatas.add(new CallItem("평가기획팀-대학자체평가,학과평가", "0516634159"));
        callDatas.add(new CallItem("입학관리처-처장", "0516634016"));
        callDatas.add(new CallItem("입학관리팀-팀장", "0516634030"));
        callDatas.add(new CallItem("입학관리팀-대표전화", "0516635555"));
        callDatas.add(new CallItem("입학관리팀-정시", "0516634031"));
        callDatas.add(new CallItem("입학관리팀-수시", "0516634032"));
        callDatas.add(new CallItem("입학관리팀-편입학", "0516634033"));
        callDatas.add(new CallItem("입학관리팀-FAX", "0516634038"));
        callDatas.add(new CallItem("대외협력처-처장", "0516634018"));
        callDatas.add(new CallItem("국제교류팀-팀장", "0516634060"));
        callDatas.add(new CallItem("국제교류팀-교환학생,MOU,하계캠프", "0516634061"));
        callDatas.add(new CallItem("국제교류팀-유학생관리,어학연수", "0516634062"));
        callDatas.add(new CallItem("국제교류팀-외국인입학,원어강의", "0516634064"));
        callDatas.add(new CallItem("국제교류팀-글로벌라운지", "0516634065"));
        callDatas.add(new CallItem("국제교류팀-상담 및 민원", "0516634067"));
        callDatas.add(new CallItem("국제교류팀-FAX", "0516634069"));
        callDatas.add(new CallItem("발전협력팀-팀장", "0516634170"));
        callDatas.add(new CallItem("발전협력팀-발전기금 모금", "0516634171"));
        callDatas.add(new CallItem("발전협력팀-발전기금 안내", "0516634172"));
        callDatas.add(new CallItem("발전협력팀-FAX", "0516634179"));
        callDatas.add(new CallItem("한국어학당-소장", "0516634018"));
        callDatas.add(new CallItem("한국어학당-팀장", "0516634060"));
        callDatas.add(new CallItem("한국어학당-행정실", "0516634063"));
        callDatas.add(new CallItem("한국어학당-상담 및 민원", "0516634068"));
        callDatas.add(new CallItem("한국어학당-FAX", "0516634069"));
        callDatas.add(new CallItem("글로벌비즈니스본부-본부장", "0516634424"));
        callDatas.add(new CallItem("글로벌비즈니스본부-행정실", "0516634161"));
        callDatas.add(new CallItem("글로벌비즈니스본부-FAX", "0516634169"));
        callDatas.add(new CallItem("글로벌비즈니스인재센터-행정실", "0516634161"));
        callDatas.add(new CallItem("글로벌비즈니스인재센터-FAX", "0516634169"));
        callDatas.add(new CallItem("행정지원처-처장", "0516634110"));
        callDatas.add(new CallItem("총무팀-팀장", "0516634080"));
        callDatas.add(new CallItem("총무팀-직원인사", "0516634081"));
        callDatas.add(new CallItem("총무팀-사학연금,4대보험", "0516634082"));
        callDatas.add(new CallItem("총무팀-차량등록,주차권발급", "0516634083"));
        callDatas.add(new CallItem("총무팀-개인정보보호,학교차량배차,민방위", "0516634085"));
        callDatas.add(new CallItem("총무팀-행정전자서명인증서", "0516634086"));
        callDatas.add(new CallItem("총무팀-우편물접수실", "0516634088"));
        callDatas.add(new CallItem("총무팀-우편취급국", "0516636557"));
        callDatas.add(new CallItem("총무팀-FAX", "0516634089"));
        callDatas.add(new CallItem("경리팀-팀장", "0516634120"));
        callDatas.add(new CallItem("경리팀-경리", "0516634121"));
        callDatas.add(new CallItem("경리팀-등록", "0516634122"));
        callDatas.add(new CallItem("경리팀-급여,연말정산", "0516634123"));
        callDatas.add(new CallItem("에코캠퍼스팀-팀장", "0516634090"));
        callDatas.add(new CallItem("에코캠퍼스팀-건설공사", "0516634091"));
        callDatas.add(new CallItem("에코캠퍼스팀-시설관리공사", "0516634092"));
        callDatas.add(new CallItem("에코캠퍼스팀-조경,환경", "0516634093"));
        callDatas.add(new CallItem("에코캠퍼스팀-설비,안전", "0516634094"));
        callDatas.add(new CallItem("에코캠퍼스팀-FAX", "0516634099"));
        callDatas.add(new CallItem("에코캠퍼스팀-고장신고", "0516635119"));
        callDatas.add(new CallItem("통합사무실-전기", "0516634991"));
        callDatas.add(new CallItem("통합사무실-목공", "0516634992"));
        callDatas.add(new CallItem("통합사무실-조경", "0516634993"));
        callDatas.add(new CallItem("통합사무실-영선", "0516634994"));
        callDatas.add(new CallItem("통합사무실-냉·난방,수도", "0516634995"));
        callDatas.add(new CallItem("통합사무실-27호관기계실", "0516634996"));
        callDatas.add(new CallItem("통합사무실-철공", "0516634997"));
        callDatas.add(new CallItem("통합사무실-FAX", "0516634999"));
        callDatas.add(new CallItem("통합사무실-미화원대기실", "0516636480"));
        callDatas.add(new CallItem("구매관재팀-팀장", "0516634130"));
        callDatas.add(new CallItem("구매관재팀-사무용구매", "0516634131"));
        callDatas.add(new CallItem("구매관재팀-교육용구매", "0516634132"));
        callDatas.add(new CallItem("구매관재팀-인쇄물", "0516634132"));
        callDatas.add(new CallItem("구매관재팀-비품통계", "0516634133"));
        callDatas.add(new CallItem("구매관재팀-비품관리", "0516634134"));
        callDatas.add(new CallItem("구매관재팀-비품수리,대여", "0516634136"));
        callDatas.add(new CallItem("구매관재팀-FAX", "0516634139"));
        callDatas.add(new CallItem("예비군연대-연대장", "0516635460"));
        callDatas.add(new CallItem("예비군연대-교육훈련", "0516635461"));
        callDatas.add(new CallItem("예비군연대-대원신고", "0516635462"));
        callDatas.add(new CallItem("예비군연대-FAX", "0516635469"));
        callDatas.add(new CallItem("수위실-제2공학관", "0516636408"));
        callDatas.add(new CallItem("수위실-멀티미디어정보관", "0516636426"));
        callDatas.add(new CallItem("수위실-중앙도서관", "0516636427"));
        callDatas.add(new CallItem("수위실-누리생활관", "0516636428"));
        callDatas.add(new CallItem("차량통제소-정문", "0516636430"));
        callDatas.add(new CallItem("차량통제소-동문", "0516636431"));
        callDatas.add(new CallItem("대기실-운전기사", "0516636433"));
        callDatas.add(new CallItem("문화홍보처-처장", "0516634019"));
        callDatas.add(new CallItem("문화기획팀-팀장", "0516635360"));
        callDatas.add(new CallItem("문화기획팀-기획,대관,행정", "0516635361"));
        callDatas.add(new CallItem("문화기획팀-문화공간시설", "0516635362"));
        callDatas.add(new CallItem("문화기획팀-미술관", "0516635363"));
        callDatas.add(new CallItem("문화기획팀-콘서트홀", "0516635365"));
        callDatas.add(new CallItem("문화기획팀-행사안내", "0516635367"));
        callDatas.add(new CallItem("문화기획팀-FAX", "0516635369"));
        callDatas.add(new CallItem("홍보팀-팀장 ", "0516634040"));
        callDatas.add(new CallItem("홍보팀-기획,보도자료", "0516634041"));
        callDatas.add(new CallItem("홍보팀-행사,광고", "0516634042"));
        callDatas.add(new CallItem("홍보팀-대학투어", "0516634043"));
        callDatas.add(new CallItem("홍보팀-디자인관리", "0516634044"));
        callDatas.add(new CallItem("홍보팀-FAX", "0516634049"));
        callDatas.add(new CallItem("조류관-관장", "0516635385"));
        callDatas.add(new CallItem("조류관-연구실", "0516635386"));
        callDatas.add(new CallItem("조류관-FAX", "0516635389"));
        callDatas.add(new CallItem("박물관-관장", "0516635380"));
        callDatas.add(new CallItem("박물관-학예연구실", "0516635381"));
        callDatas.add(new CallItem("박물관-전시실", "0516635382"));
        callDatas.add(new CallItem("박물관-FAX", "0516635383"));
        callDatas.add(new CallItem("취업진로처-처장", "0516634764"));
        callDatas.add(new CallItem("취업지원팀-팀장", "0516635350"));
        callDatas.add(new CallItem("취업지원팀-취업상담", "0516635341"));
        callDatas.add(new CallItem("취업지원팀-취업통계", "0516635351"));
        callDatas.add(new CallItem("취업지원팀-취업,부업추천", "0516635352"));
        callDatas.add(new CallItem("취업지원팀-취업교육,취업방문출장", "0516635353"));
        callDatas.add(new CallItem("취업지원팀-취업교과목", "0516635354"));
        callDatas.add(new CallItem("취업지원팀-FAX", "0516635359"));
        callDatas.add(new CallItem("진로개발팀-팀장", "0516635350"));
        callDatas.add(new CallItem("여대생커리어개발센터-소  장", "0516634764"));
        callDatas.add(new CallItem("여대생커리어개발센터-취업마일리지", "0516635343"));
        callDatas.add(new CallItem("산학협력단-단장", "0516635050"));
        callDatas.add(new CallItem("산학협력단-부단장", "0516635060"));
        callDatas.add(new CallItem("산학협력기획팀-팀장", "0516635085"));
        callDatas.add(new CallItem("산학협력기획팀-연구기획,가족기업", "0516635086"));
        callDatas.add(new CallItem("산학협력기획팀-산학통계,산학교원,협약체결(용역)", "0516635087"));
        callDatas.add(new CallItem("산학협력기획팀-사업기획,협약체결(정부부처)", "0516635089"));
        callDatas.add(new CallItem("산학협력기획팀-현장실습,홈페이지관리", "0516635084"));
        callDatas.add(new CallItem("산학협력기획팀-기타안내", "0516635090"));
        callDatas.add(new CallItem("산학협력기획팀-FAX", "0516635059"));
        callDatas.add(new CallItem("산학사업팀-팀장", "0516635085"));
        callDatas.add(new CallItem("산학사업팀-지적재산권관리,기술이전,학교기업", "0516635053"));
        callDatas.add(new CallItem("산학사업팀-특허 심의위원회", "0516635087"));
        callDatas.add(new CallItem("산학사업팀-FAX", "0516635059"));
        callDatas.add(new CallItem("연구지원관리팀-팀장", "0516635051"));
        callDatas.add(new CallItem("연구지원관리팀-교내연구(연구비,논문게재료)", "0516635052"));
        callDatas.add(new CallItem("연구지원관리팀-교외연구(정부기관,산업체)", "0516635056"));
        callDatas.add(new CallItem("연구지원관리팀-교외연구(교육부,연구재단)", "0516635057"));
        callDatas.add(new CallItem("연구지원관리팀-FAX", "0516635059"));
        callDatas.add(new CallItem("경영지원팀-팀장", "0516635051"));
        callDatas.add(new CallItem("경영지원팀-인사관리", "0516635054"));
        callDatas.add(new CallItem("경영지원팀-회계관리", "0516635058"));
        callDatas.add(new CallItem("경영지원팀-FAX", "0516635059"));
        callDatas.add(new CallItem("중소기업산학협력센터-소장", "0516635060"));
        callDatas.add(new CallItem("중소기업산학협력센터-행정실", "0516635908"));
        callDatas.add(new CallItem("중소기업산학협력센터-FAX", "0516635909"));
        callDatas.add(new CallItem("창업보육센터-소장", "0516634458"));
        callDatas.add(new CallItem("창업보육센터-행정실", "0516635901"));
        callDatas.add(new CallItem("창업보육센터-FAX", "0516635909"));
        callDatas.add(new CallItem("공동기기센터-소장", "0516634644"));
        callDatas.add(new CallItem("공동기기센터-연구지원실(지하)", "0516635480"));
        callDatas.add(new CallItem("공동기기센터-연구지원실(1층)", "0516635481"));
        callDatas.add(new CallItem("현장실습지원센터-센터장", "0516635060"));
        callDatas.add(new CallItem("현장실습지원센터-행정실", "0516635084"));
        callDatas.add(new CallItem("창업보육센터-센터장", "0516634458"));
        callDatas.add(new CallItem("창업보육센터-행정실", "0516635084"));
        callDatas.add(new CallItem("고령친화이지라이프사업단-단장", "0516634773"));
        callDatas.add(new CallItem("고령친화이지라이프사업단-행정실", "0516636593"));
        callDatas.add(new CallItem("고령친화이지라이프사업단-FAX", "0516636590"));
        callDatas.add(new CallItem("유니버설디자인연구센터-센터장", "0516635174"));
        callDatas.add(new CallItem("유니버설디자인연구센터-행정실", "0516635080"));
        callDatas.add(new CallItem("유니버설디자인연구센터-체험관", "0516635081"));
        callDatas.add(new CallItem("유니버설디자인연구센터-FAX", "0516635082"));
        callDatas.add(new CallItem("학교기업 시빅뉴스-대표", "0516635121"));
        callDatas.add(new CallItem("학교기업 시빅뉴스-편집인", "0516635126"));
        callDatas.add(new CallItem("학교기업 시빅뉴스-사무실(편집)", "0516635129"));
        callDatas.add(new CallItem("학교기업 시빅뉴스-사무실(행정 및 회계)", "0516635087"));
        callDatas.add(new CallItem("법무감사실-실장", "0516634140"));
        callDatas.add(new CallItem("법무감사실-팀장", "0516634141"));
        callDatas.add(new CallItem("법무감사실-법무,규정", "0516634142"));
        callDatas.add(new CallItem("법무감사실-감사", "0516634143"));
        callDatas.add(new CallItem("법무감사실-FAX", "0516634144"));
        callDatas.add(new CallItem("씨케이사업단-단장", "0516634014"));
        callDatas.add(new CallItem("씨케이사업단-국장", "0516634015"));
        callDatas.add(new CallItem("씨케이사업단-팀장", "0516634150"));
        callDatas.add(new CallItem("씨케이사업단-CK사무국", "0516634148"));
        callDatas.add(new CallItem("씨케이사업단-FYLs사업단", "0516635926"));
        callDatas.add(new CallItem("씨케이사업단-글로컬사업단 ", "0516635981"));
        callDatas.add(new CallItem("씨케이사업단-ICDT사업단", "0516635918"));
        callDatas.add(new CallItem("씨케이사업단-영화사업단", "0516635938"));
        callDatas.add(new CallItem("씨케이사업단-MP사업단", "0516635926"));
        callDatas.add(new CallItem("창업지원단-단장", "0516635900"));
        callDatas.add(new CallItem("창업지원단-부단장", "0516634458"));
        callDatas.add(new CallItem("창업지원단-행정실", "0516635903"));
        callDatas.add(new CallItem("경성사회봉사단-소장", "0516634012"));
        callDatas.add(new CallItem("경성사회봉사단-팀장", "0516634050"));
        callDatas.add(new CallItem("경성사회봉사단-행정실", "0516634051"));
        callDatas.add(new CallItem("학생군사교육단-단장", "0516635470"));
        callDatas.add(new CallItem("학생군사교육단-행정실", "0516635471"));
        callDatas.add(new CallItem("학생군사교육단-교관실(1)", "0516635473"));
        callDatas.add(new CallItem("학생군사교육단-교관실(2)", "0516635474"));
        callDatas.add(new CallItem("학생군사교육단-명예위원실", "0516635475"));
        callDatas.add(new CallItem("학생군사교육단-FAX", "0516635479"));
        callDatas.add(new CallItem("연구윤리센터-소장", "0516634010"));
        callDatas.add(new CallItem("연구윤리센터-행정실", "0516634181"));
        callDatas.add(new CallItem("연구윤리센터-FAX", "0516634185"));
        callDatas.add(new CallItem("중앙도서관-관장", "0516635500"));
        callDatas.add(new CallItem("학술정보지원팀-팀장 ", "0516635510"));
        callDatas.add(new CallItem("학술정보지원팀-운영지원 ", "0516635511"));
        callDatas.add(new CallItem("학술정보지원팀-기증,실험실습비도서", "0516635512"));
        callDatas.add(new CallItem("학술정보지원팀-연속간행물 구입", "0516635513"));
        callDatas.add(new CallItem("학술정보지원팀-국내도서 구입", "0516635514"));
        callDatas.add(new CallItem("학술정보지원팀-비도서 구입", "0516635515"));
        callDatas.add(new CallItem("학술정보지원팀-국외도서 구입", "0516635516"));
        callDatas.add(new CallItem("학술정보지원팀-자료조직", "0516635517"));
        callDatas.add(new CallItem("학술정보지원팀-FAX", "0516635519"));
        callDatas.add(new CallItem("학술정보서비스팀-팀장", "0516635520"));
        callDatas.add(new CallItem("학술정보서비스팀-이용교육", "0516635521"));
        callDatas.add(new CallItem("학술정보서비스팀-대출,특별허가회원관리", "0516635523"));
        callDatas.add(new CallItem("학술정보서비스팀-반납", "0516635523"));
        callDatas.add(new CallItem("학술정보서비스팀-제1주제자료실(철학,종교,사회)", "0516635526"));
        callDatas.add(new CallItem("학술정보서비스팀-제2주제자료실(언어,문학,역사)", "0516635527"));
        callDatas.add(new CallItem("학술정보서비스팀-제3주제자료실(순수,기술,예술,총류)", "0516635528"));
        callDatas.add(new CallItem("학술정보서비스팀-연속간행물실", "0516635531"));
        callDatas.add(new CallItem("학술정보서비스팀-열람실관리", "0516635532"));
        callDatas.add(new CallItem("학술정보서비스팀-정보검색", "0516635535"));
        callDatas.add(new CallItem("학술정보서비스팀-비도서대출,반납", "0516635536"));
        callDatas.add(new CallItem("학술정보서비스팀-출입안내", "0516635541"));
        callDatas.add(new CallItem("학술정보서비스팀-FAX", "0516635529"));
        callDatas.add(new CallItem("출판부-부장", "0516634830"));
        callDatas.add(new CallItem("출판부-출판", "0516634195"));
        callDatas.add(new CallItem("출판부-FAX", "0516634199"));
        callDatas.add(new CallItem("정보전산원-원장", "0516635400"));
        callDatas.add(new CallItem("개발운영팀-팀장", "0516635410"));
        callDatas.add(new CallItem("개발운영팀-경성포탈,부서정보", "0516635401"));
        callDatas.add(new CallItem("개발운영팀-수업,대학원전산지원", "0516635402"));
        callDatas.add(new CallItem("개발운영팀-인사전산지원", "0516635403"));
        callDatas.add(new CallItem("개발운영팀-전자결재", "0516635404"));
        callDatas.add(new CallItem("개발운영팀-입시전산지원", "0516635405"));
        callDatas.add(new CallItem("개발운영팀-웹메일", "0516635406"));
        callDatas.add(new CallItem("개발운영팀-도서관전산지원", "0516635407"));
        callDatas.add(new CallItem("개발운영팀-학적,장학전산지원", "0516635415"));
        callDatas.add(new CallItem("개발운영팀-SMS", "0516635424"));
        callDatas.add(new CallItem("개발운영팀-유무선네트워크장애신고", "0516635411"));
        callDatas.add(new CallItem("개발운영팀-컴퓨터관리,정보보안", "0516635413"));
        callDatas.add(new CallItem("개발운영팀-IP 관련문의", "0516635414"));
        callDatas.add(new CallItem("개발운영팀-소프트웨어대여", "0516635416"));
        callDatas.add(new CallItem("개발운영팀-컴퓨터고장신고", "0516635417"));
        callDatas.add(new CallItem("개발운영팀-홈페이지", "0516635421"));
        callDatas.add(new CallItem("개발운영팀-학과홈페이지", "0516635422"));
        callDatas.add(new CallItem("개발운영팀-FAX", "0516635409"));
        callDatas.add(new CallItem("평생교육원-원장", "0516635310"));
        callDatas.add(new CallItem("평생교육원-팀장", "0516635320"));
        callDatas.add(new CallItem("사회교육부/어학교육부-사회교육부 안내", "0516635311"));
        callDatas.add(new CallItem("사회교육부/어학교육부-경성골드에이지", "0516635312"));
        callDatas.add(new CallItem("사회교육부/어학교육부-신규과정개설,일반과정", "0516635313"));
        callDatas.add(new CallItem("사회교육부/어학교육부-외부기관사업", "0516635314"));
        callDatas.add(new CallItem("사회교육부/어학교육부-외국어,자격증과정", "0516635315"));
        callDatas.add(new CallItem("사회교육부/어학교육부-어학교육부 안내", "0516635316"));
        callDatas.add(new CallItem("사회교육부/어학교육부-토익수강 안내 ", "0516635317"));
        callDatas.add(new CallItem("사회교육부/어학교육부-산청연수원", "0559720468"));
        callDatas.add(new CallItem("사회교육부/어학교육부-FAX", "0516635319"));
        callDatas.add(new CallItem("스포츠센터-스포츠프로그램 운영", "0516635300"));
        callDatas.add(new CallItem("스포츠센터-체육시설 대관", "0516635301"));
        callDatas.add(new CallItem("스포츠센터-수납 및 회계업무", "0516635302"));
        callDatas.add(new CallItem("스포츠센터-FAX", "0516635309"));
        callDatas.add(new CallItem("엠씨씨-소장", "0516635125"));
        callDatas.add(new CallItem("엠씨씨-MCC지원실", "0516635391"));
        callDatas.add(new CallItem("엠씨씨-방송제작팀", "0516635393"));
        callDatas.add(new CallItem("엠씨씨-취재팀,웹제작팀", "0516635395"));
        callDatas.add(new CallItem("엠씨씨-MCC스튜디오", "0516635396"));
        callDatas.add(new CallItem("기념사업회/복지관-순산기념사업회", "0516634573"));
        callDatas.add(new CallItem("기념사업회/복지관-남구종합사회복지관", "0516473655"));
        callDatas.add(new CallItem("자치단체-교수회", "0516636540"));
        callDatas.add(new CallItem("자치단체-노동조합", "0516636541"));
        callDatas.add(new CallItem("자치단체-총동창회", "0516636545"));
        callDatas.add(new CallItem("자치단체-원우회", "0516636546"));
        callDatas.add(new CallItem("자치단체-민주동문회", "0516635850"));
        callDatas.add(new CallItem("대학원-대학원장", "0516635560"));
        callDatas.add(new CallItem("대학원-교학지원", "0516635572"));
        callDatas.add(new CallItem("경영대학원-대학원장", "0516634400"));
        callDatas.add(new CallItem("경영대학원-교학지원", "0516635571"));
        callDatas.add(new CallItem("경영대학원-최고경영자과정", "0516635583"));
        callDatas.add(new CallItem("경영대학원-FAX", "0516635586"));
        callDatas.add(new CallItem("예술종합대학원-대학원장", "0516635560"));
        callDatas.add(new CallItem("예술종합대학원-교학지원", "0516635575"));
        callDatas.add(new CallItem("교육대학원-대학원장", "0516635560"));
        callDatas.add(new CallItem("교육대학원-교학지원", "0516635575"));
        callDatas.add(new CallItem("사회복지대학원-대학원장", "0516635560"));
        callDatas.add(new CallItem("사회복지대학원-교학부장", "0516634548"));
        callDatas.add(new CallItem("사회복지대학원-교학지원", "0516635571"));
        callDatas.add(new CallItem("임상약학보건대학원-대학원장", "0516634800"));
        callDatas.add(new CallItem("임상약학보건대학원-교학지원", "0516635571"));
        callDatas.add(new CallItem("디지털디자인전문대학원-대학원장", "0516635560"));
        callDatas.add(new CallItem("디지털디자인전문대학원-교학지원", "0516635572"));
        callDatas.add(new CallItem("대학원통합지원팀-교학부장", "0516635577"));
        callDatas.add(new CallItem("대학원통합지원팀-교학지원(일반,디지털디자인전문대학원)", "0516635572"));
        callDatas.add(new CallItem("대학원통합지원팀-교학지원(경영,사회복지,멀티미디어 대학원)", "0516635571"));
        callDatas.add(new CallItem("대학원통합지원팀-교학지원(교육,멀티미디어 대학원)", "0516635575"));
        callDatas.add(new CallItem("대학원통합지원팀-FAX", "0516635579"));
        callDatas.add(new CallItem("문화발전연구소-소장", "0516634744"));
        callDatas.add(new CallItem("유기소자특성화연구소-소장", "0516634620"));
        callDatas.add(new CallItem("디자인&문화콘텐츠연구소-소장 ", "0516635204"));
        callDatas.add(new CallItem("정신건강상담연구소-소장", "0516634328"));
        callDatas.add(new CallItem("문과대학-학장", "0516634200"));
        callDatas.add(new CallItem("문과대학-부학장", "0516634211"));
        callDatas.add(new CallItem("문과대학-제1통합행정실팀장", "0516634202"));
        callDatas.add(new CallItem("문과대학-제1통합행정실", "0516634204"));
        callDatas.add(new CallItem("문과대학-FAX", "0516634209"));
        callDatas.add(new CallItem("문과대학-외래교수실", "0516634206"));
        callDatas.add(new CallItem("문과대학-전산실", "0516634208"));
        callDatas.add(new CallItem("인문문화학부-학부장", "0516634273"));
        callDatas.add(new CallItem("인문문화학부-학부사무실", "0516634218"));
        callDatas.add(new CallItem("인문문화학부-김무식", "0516634216"));
        callDatas.add(new CallItem("인문문화학부-나찬연", "0516634212"));
        callDatas.add(new CallItem("인문문화학부-박훈하", "0516634213"));
        callDatas.add(new CallItem("인문문화학부-황병익(국어국문학)", "0516634211"));
        callDatas.add(new CallItem("인문문화학부-정훈식", "0516635941"));
        callDatas.add(new CallItem("인문문화학부-윤영기(일어일문학)", "0516634253"));
        callDatas.add(new CallItem("인문문화학부-이충렬", "0516634254"));
        callDatas.add(new CallItem("인문문화학부-남미영", "0516634256"));
        callDatas.add(new CallItem("인문문화학부-나카지마", "0516634252"));
        callDatas.add(new CallItem("인문문화학부-김보은", "0516634252"));
        callDatas.add(new CallItem("인문문화학부-정경주", "0516634270"));
        callDatas.add(new CallItem("인문문화학부-박준원(한문학)", "0516634273"));
        callDatas.add(new CallItem("인문문화학부-김철범", "0516634272"));
        callDatas.add(new CallItem("인문문화학부-신승훈", "0516634271"));
        callDatas.add(new CallItem("인문문화학부-조창규", "0516634496"));
        callDatas.add(new CallItem("인문문화학부-강대민", "0516634285"));
        callDatas.add(new CallItem("인문문화학부-백윤목", "0516634282"));
        callDatas.add(new CallItem("인문문화학부-이경일(사학)", "0516634281"));
        callDatas.add(new CallItem("인문문화학부-이순갑", "0516634496"));
        callDatas.add(new CallItem("인문문화학부-국어국문학전공사무실", "0516634218"));
        callDatas.add(new CallItem("인문문화학부-일어일문학전공사무실", "0516634255"));
        callDatas.add(new CallItem("인문문화학부-한문학전공사무실", "0516634274"));
        callDatas.add(new CallItem("인문문화학부-사학전공사무실", "0516634286"));
        callDatas.add(new CallItem("글로컬문화학부-학부장", "0516634240"));
        callDatas.add(new CallItem("글로컬문화학부-학부사무실", "0516634230"));
        callDatas.add(new CallItem("글로컬문화학부-곽병휴", "0516634233"));
        callDatas.add(new CallItem("글로컬문화학부-김영배", "0516634295"));
        callDatas.add(new CallItem("글로컬문화학부-김재기", "0516634296"));
        callDatas.add(new CallItem("글로컬문화학부-박이도", "0516634232"));
        callDatas.add(new CallItem("글로컬문화학부-배영달", "0516634242"));
        callDatas.add(new CallItem("글로컬문화학부-손호은", "0516634231"));
        callDatas.add(new CallItem("글로컬문화학부-이승대", "0516634240"));
        callDatas.add(new CallItem("글로컬문화학부-전영갑", "0516634293"));
        callDatas.add(new CallItem("글로컬문화학부-정을미", "0516634244"));
        callDatas.add(new CallItem("글로컬문화학부-배학수", "0516634290"));
        callDatas.add(new CallItem("글로컬문화학부-레쟈느", "0516634241"));
        callDatas.add(new CallItem("글로컬문화학부-김영리", "0516634497"));
        callDatas.add(new CallItem("글로컬문화학부-김 지 영", "0516634497"));
        callDatas.add(new CallItem("글로컬문화학부-전일수", "0516634489"));
        callDatas.add(new CallItem("글로컬문화학부-신응철", "0516635941"));
        callDatas.add(new CallItem("글로컬문화학부-윤태원", "0516635941"));
        callDatas.add(new CallItem("글로컬문화학부-학과사무실", "0516634235"));
        callDatas.add(new CallItem("철학과-배학수", "0516634290"));
        callDatas.add(new CallItem("철학과-학과사무실", "0516634235"));
        callDatas.add(new CallItem("영어영문학과-최수연", "0516634223"));
        callDatas.add(new CallItem("영어영문학과-나병우", "0516634225"));
        callDatas.add(new CallItem("영어영문학과-이현석", "0516634226"));
        callDatas.add(new CallItem("영어영문학과-심미현 ", "0516634228"));
        callDatas.add(new CallItem("영어영문학과-김철규", "0516634222"));
        callDatas.add(new CallItem("영어영문학과-정철민", "0516634220"));
        callDatas.add(new CallItem("영어영문학과-드웨인스톨스", "0516634224"));
        callDatas.add(new CallItem("영어영문학과-리더", "0516634221"));
        callDatas.add(new CallItem("영어영문학과-강경호", "0516634496"));
        callDatas.add(new CallItem("영어영문학과-학과사무실", "0516634236"));
        callDatas.add(new CallItem("중국학과-이재하", "0516634264"));
        callDatas.add(new CallItem("중국학과-류영표", "0516634260"));
        callDatas.add(new CallItem("중국학과-오창화", "0516634263"));
        callDatas.add(new CallItem("중국학과-하영삼", "0516634266"));
        callDatas.add(new CallItem("중국학과-이화범", "0516634267"));
        callDatas.add(new CallItem("중국학과-곽복선", "0516634371"));
        callDatas.add(new CallItem("중국학과-임형석", "0516634355"));
        callDatas.add(new CallItem("중국학과-양난", "0516634357"));
        callDatas.add(new CallItem("중국학과-여정화", "0516634357"));
        callDatas.add(new CallItem("중국학과-김화영", "0516634357"));
        callDatas.add(new CallItem("중국학과-외래교수실", "0516634356"));
        callDatas.add(new CallItem("중국학과-학과사무실", "0516634265"));
        callDatas.add(new CallItem("중국학과-중어중문학,중국어통번역학", "0516634360"));
        callDatas.add(new CallItem("중국학과-중국통상학과사무실", "0516634360"));
        callDatas.add(new CallItem("문헌정보학과-김영기", "0516634311"));
        callDatas.add(new CallItem("문헌정보학과-이종문", "0516634315"));
        callDatas.add(new CallItem("문헌정보학과-김선애", "0516634310"));
        callDatas.add(new CallItem("문헌정보학과-정종기", "0516634312"));
        callDatas.add(new CallItem("문헌정보학과-학과사무실", "0516634314"));
        callDatas.add(new CallItem("교육학과-김희복  ", "0516634323"));
        callDatas.add(new CallItem("교육학과-강성빈", "0516634325"));
        callDatas.add(new CallItem("교육학과-권형규", "0516634329"));
        callDatas.add(new CallItem("교육학과-천성문", "0516634328"));
        callDatas.add(new CallItem("교육학과-주용국", "0516634321"));
        callDatas.add(new CallItem("교육학과-정숙자", "0516634499"));
        callDatas.add(new CallItem("교육학과-김미옥", "0516634499"));
        callDatas.add(new CallItem("교육학과-이지경", "0516634499"));
        callDatas.add(new CallItem("교육학과-장소은", "0516634499"));
        callDatas.add(new CallItem("교육학과-학과사무실 ", "0516634327"));
        callDatas.add(new CallItem("유아교육과-홍 순 옥", "0516634332"));
        callDatas.add(new CallItem("유아교육과-이연승", "0516634330"));
        callDatas.add(new CallItem("유아교육과-서현아 ", "0516634333"));
        callDatas.add(new CallItem("유아교육과-정지현", "0516634331"));
        callDatas.add(new CallItem("유아교육과-이종길", "0516635941"));
        callDatas.add(new CallItem("유아교육과-학과사무실", "0516634335"));
        callDatas.add(new CallItem("윤리교육과-조경근", "0516634345"));
        callDatas.add(new CallItem("윤리교육과-장세호", "0516634340"));
        callDatas.add(new CallItem("윤리교육과-박장호", "0516634342"));
        callDatas.add(new CallItem("윤리교육과-김상돈", "0516634343"));
        callDatas.add(new CallItem("윤리교육과-학과사무실", "0516634346"));
        callDatas.add(new CallItem("교육인재개발연구소-소장", "0516634325"));
        callDatas.add(new CallItem("영재뇌교육연구소-소장", "0516634329"));
        callDatas.add(new CallItem("유아창의인성교육연구소-소장", "0516634333"));
        callDatas.add(new CallItem("독서교육문화연구소-소장", "0516634315"));
        callDatas.add(new CallItem("글로벌차이나연구소-소장", "0516634371"));
        callDatas.add(new CallItem("법정대학-학장", "0516634500"));
        callDatas.add(new CallItem("법정대학-부학장", "0516635135"));
        callDatas.add(new CallItem("법정대학-제2통합행정실팀장", "0516634602"));
        callDatas.add(new CallItem("법정대학-제2통합행정실", "0516634502"));
        callDatas.add(new CallItem("법정대학-제2통합행정실", "0516634609"));
        callDatas.add(new CallItem("법정대학-FAX", "0516634509"));
        callDatas.add(new CallItem("법정대학-외래교수실", "0516634507"));
        callDatas.add(new CallItem("법정대학-전산실", "0516634508"));
        callDatas.add(new CallItem("법행정정치학부-학 부 장", "0516634521"));
        callDatas.add(new CallItem("법행정정치학부-학부사무실", "0516634505"));
        callDatas.add(new CallItem("법행정정치학부-최문기", "0516634511"));
        callDatas.add(new CallItem("법행정정치학부-왕순모", "0516634512"));
        callDatas.add(new CallItem("법행정정치학부-심재무", "0516634518"));
        callDatas.add(new CallItem("법행정정치학부-박은경", "0516634513"));
        callDatas.add(new CallItem("법행정정치학부-이우석(법학)", "0516634514"));
        callDatas.add(new CallItem("법행정정치학부-손형섭", "0516634516"));
        callDatas.add(new CallItem("법행정정치학부-김주학", "0516634489"));
        callDatas.add(new CallItem("법행정정치학부-송근원", "0516634524"));
        callDatas.add(new CallItem("법행정정치학부-나중식", "0516634527"));
        callDatas.add(new CallItem("법행정정치학부-황수연", "0516634523"));
        callDatas.add(new CallItem("법행정정치학부-배준구", "0516634525"));
        callDatas.add(new CallItem("법행정정치학부-정충식", "0516634528"));
        callDatas.add(new CallItem("법행정정치학부-문유석(행정학)", "0516634521"));
        callDatas.add(new CallItem("법행정정치학부-한동호", "0516634526"));
        callDatas.add(new CallItem("법행정정치학부-송이재", "0516634494"));
        callDatas.add(new CallItem("법행정정치학부-크리스토퍼", "0516634522"));
        callDatas.add(new CallItem("법행정정치학부-공보경", "0516634534"));
        callDatas.add(new CallItem("법행정정치학부-권용립", "0516634531"));
        callDatas.add(new CallItem("법행정정치학부-안철현(정치외교학)", "0516634532"));
        callDatas.add(new CallItem("법행정정치학부-법학전공사무실", "0516634510"));
        callDatas.add(new CallItem("법행정정치학부-행정학전공사무실", "0516634520"));
        callDatas.add(new CallItem("법행정정치학부-정치외교학전공사무실", "0516634530"));
        callDatas.add(new CallItem("커뮤니케이션학부-학부장", "0516635135"));
        callDatas.add(new CallItem("커뮤니케이션학부-학부사무실", "0516635120"));
        callDatas.add(new CallItem("커뮤니케이션학부-정태철", "0516635121"));
        callDatas.add(new CallItem("커뮤니케이션학부-신병률(신문방송학)", "0516635124"));
        callDatas.add(new CallItem("커뮤니케이션학부-정일형", "0516635125"));
        callDatas.add(new CallItem("커뮤니케이션학부-양혜승 ", "0516635123"));
        callDatas.add(new CallItem("커뮤니케이션학부-강성보", "0516635127"));
        callDatas.add(new CallItem("커뮤니케이션학부-강동수", "0516635945"));
        callDatas.add(new CallItem("커뮤니케이션학부-크리스티안", "0516635127"));
        callDatas.add(new CallItem("커뮤니케이션학부-황지영", "0516635132"));
        callDatas.add(new CallItem("커뮤니케이션학부-박기철", "0516635133"));
        callDatas.add(new CallItem("커뮤니케이션학부-송기인", "0516635134"));
        callDatas.add(new CallItem("커뮤니케이션학부-남경태(광고홍보학)", "0516635135"));
        callDatas.add(new CallItem("커뮤니케이션학부-이재봉", "0516635945"));
        callDatas.add(new CallItem("커뮤니케이션학부-신문방송학전공사무실", "0516635120"));
        callDatas.add(new CallItem("커뮤니케이션학부-광고홍보학전공사무실", "0516635130"));
        callDatas.add(new CallItem("사회복지학과-김수환 ", "0516634541"));
        callDatas.add(new CallItem("사회복지학과-김수영", "0516634542"));
        callDatas.add(new CallItem("사회복지학과-김영종", "0516634543"));
        callDatas.add(new CallItem("사회복지학과-손광훈 ", "0516634546"));
        callDatas.add(new CallItem("사회복지학과-정규석", "0516634547"));
        callDatas.add(new CallItem("사회복지학과-진재문", "0516634544"));
        callDatas.add(new CallItem("사회복지학과-최말옥", "0516634548"));
        callDatas.add(new CallItem("사회복지학과-장수지", "0516634549"));
        callDatas.add(new CallItem("사회복지학과-정민경", "0516634492"));
        callDatas.add(new CallItem("사회복지학과-손태홍", "0516634486"));
        callDatas.add(new CallItem("사회복지학과-변영우", "0516634486"));
        callDatas.add(new CallItem("사회복지학과-학과사무실", "0516634540"));
        callDatas.add(new CallItem("법학연구소-소장", "0516634513"));
        callDatas.add(new CallItem("법학연구소-연구실", "0516634519"));
        callDatas.add(new CallItem("상경대학-학장", "0516634400"));
        callDatas.add(new CallItem("상경대학-부학장", "0516634553"));
        callDatas.add(new CallItem("상경대학-제3통합행정실팀장", "0516634402"));
        callDatas.add(new CallItem("상경대학-제3통합행정실", "0516634404"));
        callDatas.add(new CallItem("상경대학-FAX", "0516634409"));
        callDatas.add(new CallItem("상경대학-외래교수실", "0516634407"));
        callDatas.add(new CallItem("상경대학-전산실", "0516634408"));
        callDatas.add(new CallItem("경제금융물류학부-학부장", "0516634553"));
        callDatas.add(new CallItem("경제금융물류학부-학부사무실", "0516634417"));
        callDatas.add(new CallItem("경제금융물류학부-장대익", "0516634416"));
        callDatas.add(new CallItem("경제금융물류학부-최진배", "0516634412"));
        callDatas.add(new CallItem("경제금융물류학부-이재희", "0516634410"));
        callDatas.add(new CallItem("경제금융물류학부-변대호", "0516634452"));
        callDatas.add(new CallItem("경제금융물류학부-김종한(경제금융학)", "0516634411"));
        callDatas.add(new CallItem("경제금융물류학부-김명록", "0516634457"));
        callDatas.add(new CallItem("경제금융물류학부-한윤환", "0516634455"));
        callDatas.add(new CallItem("경제금융물류학부-김태훈", "0516634456"));
        callDatas.add(new CallItem("경제금융물류학부-양재훈(물류학)", "0516634553"));
        callDatas.add(new CallItem("경제금융물류학부-이남형", "0516634481"));
        callDatas.add(new CallItem("경제금융물류학부-강현구", "0516634482"));
        callDatas.add(new CallItem("경제금융물류학부-권용배", "0516634488"));
        callDatas.add(new CallItem("경제금융물류학부-조병도", "0516634491"));
        callDatas.add(new CallItem("경제금융물류학부-경제금융학전공사무실", "0516634417"));
        callDatas.add(new CallItem("경제금융물류학부-물류학전공사무실", "0516634550"));
        callDatas.add(new CallItem("호텔관광외식경영학부-학부장", "0516634461"));
        callDatas.add(new CallItem("호텔관광외식경영학부-학부사무실", "0516634460"));
        callDatas.add(new CallItem("호텔관광외식경영학부-오흥철(호텔관광경역학)", "0516634461"));
        callDatas.add(new CallItem("호텔관광외식경영학부-이미순", "0516634463"));
        callDatas.add(new CallItem("호텔관광외식경영학부-이해영", "0516634465"));
        callDatas.add(new CallItem("호텔관광외식경영학부-얼리드", "0516634462"));
        callDatas.add(new CallItem("호텔관광외식경영학부-한은진", "0516634464"));
        callDatas.add(new CallItem("호텔관광외식경영학부-함성필", "0516634471"));
        callDatas.add(new CallItem("호텔관광외식경영학부-이종호(외식서비스경영학)", "0516634472"));
        callDatas.add(new CallItem("호텔관광외식경영학부-김학선", "0516634473"));
        callDatas.add(new CallItem("호텔관광외식경영학부-이상묵", "0516634474"));
        callDatas.add(new CallItem("호텔관광외식경영학부-호텔관광경영학전공사무실", "0516634460"));
        callDatas.add(new CallItem("호텔관광외식경영학부-외식서비스경영학전공사무실", "0516634470"));
        callDatas.add(new CallItem("경영학과-정기호", "0516634451"));
        callDatas.add(new CallItem("경영학과-이명철", "0516634438"));
        callDatas.add(new CallItem("경영학과-이상식", "0516634432"));
        callDatas.add(new CallItem("경영학과-김후곤", "0516634453"));
        callDatas.add(new CallItem("경영학과-이준섭", "0516634454"));
        callDatas.add(new CallItem("경영학과-정동섭", "0516634437"));
        callDatas.add(new CallItem("경영학과-김천길", "0516634433"));
        callDatas.add(new CallItem("경영학과-성민", "0516634435"));
        callDatas.add(new CallItem("경영학과-김종호", "0516634458"));
        callDatas.add(new CallItem("경영학과-조정은", "0516634431"));
        callDatas.add(new CallItem("경영학과-변영태", "0516634434"));
        callDatas.add(new CallItem("경영학과-이태섭", "0516634483"));
        callDatas.add(new CallItem("경영학과-강동우", "0516634489"));
        callDatas.add(new CallItem("경영학과-이경호", "0516634488"));
        callDatas.add(new CallItem("경영학과-박병석", "0516634482"));
        callDatas.add(new CallItem("경영학과-옥주수", "0516634482"));
        callDatas.add(new CallItem("경영학과-안상모", "0516634489"));
        callDatas.add(new CallItem("경영학과-백수정", "0516634477"));
        callDatas.add(new CallItem("경영학과-이희옥", "0516634495"));
        callDatas.add(new CallItem("경영학과-안경관", "0516634758"));
        callDatas.add(new CallItem("경영학과-이두원", "0516634491"));
        callDatas.add(new CallItem("경영학과-비숍", "0516634439"));
        callDatas.add(new CallItem("경영학과-학과사무실", "0516634430"));
        callDatas.add(new CallItem("국제무역통상학과-권융", "0516634428"));
        callDatas.add(new CallItem("국제무역통상학과-정병우", "0516634424"));
        callDatas.add(new CallItem("국제무역통상학과-박성익", "0516634421"));
        callDatas.add(new CallItem("국제무역통상학과-이우영", "0516634422"));
        callDatas.add(new CallItem("국제무역통상학과-강진욱", "0516634426"));
        callDatas.add(new CallItem("국제무역통상학과-전용복", "0516634423"));
        callDatas.add(new CallItem("국제무역통상학과-김연준", "0516634425"));
        callDatas.add(new CallItem("국제무역통상학과-서영순", "0516634429"));
        callDatas.add(new CallItem("국제무역통상학과-이효상", "0516634490"));
        callDatas.add(new CallItem("국제무역통상학과-한우창", "0516634476"));
        callDatas.add(new CallItem("국제무역통상학과-노정도", "0516634490"));
        callDatas.add(new CallItem("국제무역통상학과-이종진", "0516634484"));
        callDatas.add(new CallItem("국제무역통상학과-노환용", "0516635952"));
        callDatas.add(new CallItem("국제무역통상학과-정문선", "0516635952"));
        callDatas.add(new CallItem("국제무역통상학과-남상달", "0516634490"));
        callDatas.add(new CallItem("국제무역통상학과-학과사무실", "0516634427"));
        callDatas.add(new CallItem("회계학과-정명환", "0516634445"));
        callDatas.add(new CallItem("회계학과-서수덕", "0516634444"));
        callDatas.add(new CallItem("회계학과-이종태", "0516634446"));
        callDatas.add(new CallItem("회계학과-김형순", "0516634441"));
        callDatas.add(new CallItem("회계학과-윤성용", "0516634443"));
        callDatas.add(new CallItem("회계학과-추헌무", "0516634476"));
        callDatas.add(new CallItem("회계학과-배후석", "0516634494"));
        callDatas.add(new CallItem("회계학과-방신석", "0516634758"));
        callDatas.add(new CallItem("회계학과-데이비드", "0516634447"));
        callDatas.add(new CallItem("회계학과-학과사무실", "0516634440"));
        callDatas.add(new CallItem("부동산도시연구소-소장", "0516634412"));
        callDatas.add(new CallItem("이과대학-학장", "0516634600"));
        callDatas.add(new CallItem("이과대학-부학장", "0516634682"));
        callDatas.add(new CallItem("이과대학-제2통합행정실팀장", "0516634602"));
        callDatas.add(new CallItem("이과대학-제2통합행정실", "0516634604"));
        callDatas.add(new CallItem("이과대학-제2통합행정실", "0516634609"));
        callDatas.add(new CallItem("이과대학-FAX", "0516634509"));
        callDatas.add(new CallItem("이과대학-외래교수실", "0516634607"));
        callDatas.add(new CallItem("이과대학-전산실", "0516634608"));
        callDatas.add(new CallItem("수학응용통계학부-학부장", "0516634682"));
        callDatas.add(new CallItem("수학응용통계학부-학부사무실", "0516634610"));
        callDatas.add(new CallItem("수학응용통계학부-한응섭", "0516634616"));
        callDatas.add(new CallItem("이병수수학응용통계학부-", "0516634613"));
        callDatas.add(new CallItem("수학응용통계학부-권오상", "0516634611"));
        callDatas.add(new CallItem("수학응용통계학부-김재겸", "0516634612"));
        callDatas.add(new CallItem("수학응용통계학부-김승원(수학전공)", "0516634618"));
        callDatas.add(new CallItem("수학응용통계학부-정영우", "0516635951"));
        callDatas.add(new CallItem("수학응용통계학부-심영재", "0516635950"));
        callDatas.add(new CallItem("수학응용통계학부-조재근", "0516634670"));
        callDatas.add(new CallItem("수학응용통계학부-조장식", "0516634674"));
        callDatas.add(new CallItem("수학응용통계학부-정기문(응용통계학)", "0516634682"));
        callDatas.add(new CallItem("수학응용통계학부-김미정", "0516635951"));
        callDatas.add(new CallItem("수학응용통계학부-수학전공사무실", "0516634610"));
        callDatas.add(new CallItem("수학응용통계학부-응용통계학전공사무실", "0516634680"));
        callDatas.add(new CallItem("화학생명과학부-학부장", "0516634640"));
        callDatas.add(new CallItem("화학생명과학부-학부사무실", "0516634630"));
        callDatas.add(new CallItem("화학생명과학부-박동규", "0516634634"));
        callDatas.add(new CallItem("화학생명과학부-강대복", "0516634635"));
        callDatas.add(new CallItem("화학생명과학부-권태우", "0516634637"));
        callDatas.add(new CallItem("화학생명과학부-안택(화학전공)", "0516634632"));
        callDatas.add(new CallItem("화학생명과학부-김영복", "0516634478"));
        callDatas.add(new CallItem("화학생명과학부-오명진", "0516634495"));
        callDatas.add(new CallItem("화학생명과학부-문성기", "0516634641"));
        callDatas.add(new CallItem("화학생명과학부-이천복(생명과학전공)", "0516634640"));
        callDatas.add(new CallItem("화학생명과학부-윤명희", "0516634642"));
        callDatas.add(new CallItem("화학생명과학부-조영근", "0516634643"));
        callDatas.add(new CallItem("화학생명과학부-이대원", "0516634644"));
        callDatas.add(new CallItem("화학생명과학부-화학전공사무실 ", "0516634630"));
        callDatas.add(new CallItem("화학생명과학부-생명과학전공사무실", "0516634645"));
        callDatas.add(new CallItem("에너지과학과-이정식", "0516634622"));
        callDatas.add(new CallItem("에너지과학과-조성진", "0516634620"));
        callDatas.add(new CallItem("에너지과학과-이원명", "0516634623"));
        callDatas.add(new CallItem("에너지과학과-권병성", "0516634624"));
        callDatas.add(new CallItem("에너지과학과-하태욱", "0516635950"));
        callDatas.add(new CallItem("에너지과학과-학과사무실", "0516634625"));
        callDatas.add(new CallItem("물리치료학과-한진태", "0516634871"));
        callDatas.add(new CallItem("물리치료학과-구현모", "0516634872"));
        callDatas.add(new CallItem("물리치료학과-이상열", "0516634873"));
        callDatas.add(new CallItem("물리치료학과-이은주", "0516634479"));
        callDatas.add(new CallItem("물리치료학과-학과사무실", "0516634870"));
        callDatas.add(new CallItem("간호학과-최원희", "0516634862"));
        callDatas.add(new CallItem("간호학과-구혜자", "0516634863"));
        callDatas.add(new CallItem("간호학과-박지연", "0516634861"));
        callDatas.add(new CallItem("간호학과-윤혜선", "0516634864"));
        callDatas.add(new CallItem("간호학과-이재영", "0516634865"));
        callDatas.add(new CallItem("간호학과-이성화", "0516635942"));
        callDatas.add(new CallItem("간호학과-학과사무실", "0516634860"));
        callDatas.add(new CallItem("식품영양건강생활학과-학과사무실", "0516634655"));
        callDatas.add(new CallItem("기초과학연구소-소장", "0516634612"));
        callDatas.add(new CallItem("공과대학-학장", "0516634700"));
        callDatas.add(new CallItem("공과대학-부학장", "0516634768"));
        callDatas.add(new CallItem("공과대학-제1통합행정실팀장", "0516634202"));
        callDatas.add(new CallItem("공과대학-제1통합행정실", "0516634702"));
        callDatas.add(new CallItem("공과대학-FAX", "0516634709"));
        callDatas.add(new CallItem("공과대학-외래교수실", "0516634707"));
        callDatas.add(new CallItem("공과대학-전산실", "0516634708"));
        callDatas.add(new CallItem("건설환경도시공학부-학부장", "0516634792"));
        callDatas.add(new CallItem("건설환경도시공학부-학부사무실", "0516634730"));
        callDatas.add(new CallItem("건설환경도시공학부-이재복(환경공학)", "0516634733"));
        callDatas.add(new CallItem("건설환경도시공학부-엄태규", "0516634732"));
        callDatas.add(new CallItem("건설환경도시공학부-정장표", "0516634734"));
        callDatas.add(new CallItem("건설환경도시공학부-신용섭", "0516634735"));
        callDatas.add(new CallItem("건설환경도시공학부-신현무", "0516634736"));
        callDatas.add(new CallItem("건설환경도시공학부-임수빈", "0516634737"));
        callDatas.add(new CallItem("건설환경도시공학부-김해창", "0516634738"));
        callDatas.add(new CallItem("건설환경도시공학부-김충호", "0516634751"));
        callDatas.add(new CallItem("건설환경도시공학부-윤종태", "0516634752"));
        callDatas.add(new CallItem("건설환경도시공학부-김성도", "0516634754"));
        callDatas.add(new CallItem("건설환경도시공학부-최용규", "0516634753"));
        callDatas.add(new CallItem("건설환경도시공학부-이남주", "0516634755"));
        callDatas.add(new CallItem("건설환경도시공학부-문도영(토목공학)", "0516634756"));
        callDatas.add(new CallItem("건설환경도시공학부-박충환", "0516634757"));
        callDatas.add(new CallItem("건설환경도시공학부-김민수", "0516634790"));
        callDatas.add(new CallItem("건설환경도시공학부-이석환", "0516634791"));
        callDatas.add(new CallItem("건설환경도시공학부-강동진(도시공학)", "0516634792"));
        callDatas.add(new CallItem("건설환경도시공학부-남광우", "0516634794"));
        callDatas.add(new CallItem("건설환경도시공학부-신강원", "0516634793"));
        callDatas.add(new CallItem("건설환경도시공학부-이승희", "0516634492"));
        callDatas.add(new CallItem("건설환경도시공학부-김재민", "0516634489"));
        callDatas.add(new CallItem("건설환경도시공학부-손봉균", "0516634491"));
        callDatas.add(new CallItem("건설환경도시공학부-윤병호", "0516634491"));
        callDatas.add(new CallItem("건설환경도시공학부-환경공학전공사무실", "0516634730"));
        callDatas.add(new CallItem("건설환경도시공학부-토목공학전공사무실", "0516634750"));
        callDatas.add(new CallItem("건설환경도시공학부-도시공학전공사무실", "0516634795"));
        callDatas.add(new CallItem("전기전자공학부-학부장", "0516634768"));
        callDatas.add(new CallItem("전기전자공학부-학부사무실", "0516634770"));
        callDatas.add(new CallItem("전기전자공학부-윤병우", "0516634775"));
        callDatas.add(new CallItem("전기전자공학부-송종관", "0516634776"));
        callDatas.add(new CallItem("전기전자공학부-김성만", "0516634778"));
        callDatas.add(new CallItem("전기전자공학부-박장식(전자공학)", "0516634768"));
        callDatas.add(new CallItem("전기전자공학부-김상열", "0516634689"));
        callDatas.add(new CallItem("전기전자공학부-곽동주", "0516634771"));
        callDatas.add(new CallItem("전기전자공학부-이명규", "0516634772"));
        callDatas.add(new CallItem("전기전자공학부-정정원", "0516634774"));
        callDatas.add(new CallItem("전기전자공학부-성열문", "0516634777"));
        callDatas.add(new CallItem("전기전자공학부-최군호(전기공학)", "0516634798"));
        callDatas.add(new CallItem("전기전자공학부-이홍규", "0516634489"));
        callDatas.add(new CallItem("전기전자공학부-김두환", "0516634687"));
        callDatas.add(new CallItem("전기전자공학부-김준환", "0516634491"));
        callDatas.add(new CallItem("전기전자공학부-송미화", "0516634487"));
        callDatas.add(new CallItem("전기전자공학부-전자공학전공사무실", "0516634769"));
        callDatas.add(new CallItem("전기전자공학부-전기공학전공사무실", "0516634770"));
        callDatas.add(new CallItem("융합시스템공학부-학부장", "0516634724"));
        callDatas.add(new CallItem("융합시스템공학부-학부사무실", "0516634720"));
        callDatas.add(new CallItem("융합시스템공학부-안진우", "0516634773"));
        callDatas.add(new CallItem("융합시스템공학부-이동희", "0516634693"));
        callDatas.add(new CallItem("융합시스템공학부-김병호", "0516634692"));
        callDatas.add(new CallItem("융합시스템공학부-제우성(메카트로닉스)", "0516634691"));
        callDatas.add(new CallItem("융합시스템공학부-김정현", "0516634694"));
        callDatas.add(new CallItem("융합시스템공학부-최주용", "0516634695"));
        callDatas.add(new CallItem("융합시스템공학부-수레쉬", "0516634387"));
        callDatas.add(new CallItem("융합시스템공학부-바스카라", "0516634387"));
        callDatas.add(new CallItem("융합시스템공학부-서진요", "0516634387"));
        callDatas.add(new CallItem("융합시스템공학부-김홍배", "0516634721"));
        callDatas.add(new CallItem("융합시스템공학부-양성민", "0516634722"));
        callDatas.add(new CallItem("융합시스템공학부-차명수", "0516634723"));
        callDatas.add(new CallItem("융합시스템공학부-고창성(산업경영)", "0516634724"));
        callDatas.add(new CallItem("융합시스템공학부-김태운", "0516634726"));
        callDatas.add(new CallItem("융합시스템공학부-정홍인", "0516634725"));
        callDatas.add(new CallItem("융합시스템공학부-이성훈", "0516634727"));
        callDatas.add(new CallItem("융합시스템공학부-김용후", "0516634491"));
        callDatas.add(new CallItem("융합시스템공학부-이해경", "0516634498"));
        callDatas.add(new CallItem("융합시스템공학부-정원재", "0516634489"));
        callDatas.add(new CallItem("융합시스템공학부-메카트로닉스전공사무실", "0516634690"));
        callDatas.add(new CallItem("융합시스템공학부-산업경영전공사무실", "0516634720"));
        callDatas.add(new CallItem("식품응용공학부-학부장", "0516634711"));
        callDatas.add(new CallItem("식품응용공학부-학부사무실", "0516634710"));
        callDatas.add(new CallItem("식품응용공학부-이진호", "0516634716"));
        callDatas.add(new CallItem("식품응용공학부-정종연(식품생명공학)", "0516634711"));
        callDatas.add(new CallItem("식품응용공학부-김영화", "0516634652"));
        callDatas.add(new CallItem("식품응용공학부-정경화", "0516634718"));
        callDatas.add(new CallItem("식품응용공학부-조용철", "0516634718"));
        callDatas.add(new CallItem("식품응용공학부-노재경(식품영양학)＊", "0516634651"));
        callDatas.add(new CallItem("식품응용공학부-강혜경", "0516634658"));
        callDatas.add(new CallItem("식품응용공학부-이경옥", "0516634658"));
        callDatas.add(new CallItem("식품응용공학부-오초롱", "0516634658"));
        callDatas.add(new CallItem("식품응용공학부-식품생명공학전공사무실", "0516634710"));
        callDatas.add(new CallItem("식품응용공학부-식품영양학전공사무실", "0516634655"));
        callDatas.add(new CallItem("건축디자인학부-학부장", "0516634746"));
        callDatas.add(new CallItem("건축디자인학부-학부사무실", "0516634740"));
        callDatas.add(new CallItem("건축디자인학부-강혁", "0516634744"));
        callDatas.add(new CallItem("건축디자인학부-김진원", "0516634743"));
        callDatas.add(new CallItem("건축디자인학부-현철(건축학)", "0516634746"));
        callDatas.add(new CallItem("건축디자인학부-정연근", "0516634742"));
        callDatas.add(new CallItem("건축디자인학부-김종국", "0516634745"));
        callDatas.add(new CallItem("건축디자인학부-신선화", "0516634477"));
        callDatas.add(new CallItem("건축디자인학부-하호진", "0516634496"));
        callDatas.add(new CallItem("건축디자인학부-이창노(실내건축디자인학)", "0516635167"));
        callDatas.add(new CallItem("건축디자인학부-오선애", "0516635168"));
        callDatas.add(new CallItem("건축디자인학부-최강림", "0516635166"));
        callDatas.add(new CallItem("건축디자인학부-강재영", "0516634489"));
        callDatas.add(new CallItem("건축디자인학부-건축학전공사무실", "0516634740"));
        callDatas.add(new CallItem("건축디자인학부-실내건축디자인학전공사무실", "0516635165"));
        callDatas.add(new CallItem("컴퓨터공학과-양태천", "0516635141"));
        callDatas.add(new CallItem("컴퓨터공학과-성낙운", "0516635142"));
        callDatas.add(new CallItem("컴퓨터공학과-김영택", "0516635143"));
        callDatas.add(new CallItem("컴퓨터공학과-이종혁", "0516634781"));
        callDatas.add(new CallItem("컴퓨터공학과-양희재", "0516634783"));
        callDatas.add(new CallItem("컴퓨터공학과-권중장", "0516634784"));
        callDatas.add(new CallItem("컴퓨터공학과-김진천", "0516634785"));
        callDatas.add(new CallItem("컴퓨터공학과-최재원", "0516634786"));
        callDatas.add(new CallItem("컴퓨터공학과-홍석희", "0516635145"));
        callDatas.add(new CallItem("컴퓨터공학과-변석우", "0516635144"));
        callDatas.add(new CallItem("컴퓨터공학과-지상문", "0516635146"));
        callDatas.add(new CallItem("컴퓨터공학과-김병호", "0516634787"));
        callDatas.add(new CallItem("컴퓨터공학과-강인수", "0516635147"));
        callDatas.add(new CallItem("컴퓨터공학과-김주연", "0516634487"));
        callDatas.add(new CallItem("컴퓨터공학과-전주현", "0516634498"));
        callDatas.add(new CallItem("컴퓨터공학과-학과사무실", "0516634780"));
        callDatas.add(new CallItem("정보통신공학과-신광호", "0516635152"));
        callDatas.add(new CallItem("정보통신공학과-김진우", "0516635153"));
        callDatas.add(new CallItem("정보통신공학과-김종성", "0516635154"));
        callDatas.add(new CallItem("정보통신공학과-학과사무실", "0516635150"));
        callDatas.add(new CallItem("신소재공학과-최태운", "0516634764"));
        callDatas.add(new CallItem("신소재공학과-이세종", "0516634762"));
        callDatas.add(new CallItem("신소재공학과-이주신", "0516634763"));
        callDatas.add(new CallItem("신소재공학과-박민우", "0516634765"));
        callDatas.add(new CallItem("신소재공학과-안영철", "0516634485"));
        callDatas.add(new CallItem("신소재공학과-김충기", "0516634493"));
        callDatas.add(new CallItem("신소재공학과-학과사무실", "0516634760"));
        callDatas.add(new CallItem("식품생명과학연구소-소장", "0516634711"));
        callDatas.add(new CallItem("스마트메카트로닉스연구소소장", "0516634773"));
        callDatas.add(new CallItem("보안문제연구소-소장", "0516634786"));
        callDatas.add(new CallItem("약학대학-학장", "0516634800"));
        callDatas.add(new CallItem("약학대학-제1통합행정실팀장", "0516634202"));
        callDatas.add(new CallItem("약학대학-제1통합행정실", "0516634802"));
        callDatas.add(new CallItem("약학대학-FAX", "0516634809"));
        callDatas.add(new CallItem("약학대학-전산실", "0516634808"));
        callDatas.add(new CallItem("약학과-문경호", "0516634885"));
        callDatas.add(new CallItem("약학과-신영희", "0516634886"));
        callDatas.add(new CallItem("약학과-강재선", "0516634882"));
        callDatas.add(new CallItem("약학과-손기호", "0516634887"));
        callDatas.add(new CallItem("약학과-백인환", "0516634880"));
        callDatas.add(new CallItem("약학과-표재성", "0516634881"));
        callDatas.add(new CallItem("약학과-곽재환", "0516634889"));
        callDatas.add(new CallItem("약학과-김혜경", "0516634883"));
        callDatas.add(new CallItem("약학과-최윤림", "0516635942"));
        callDatas.add(new CallItem("약학과-학과사무실", "0516634888"));
        callDatas.add(new CallItem("약학연구소-소장", "0516634886"));
        callDatas.add(new CallItem("예술종합대학-학장", "0516634900"));
        callDatas.add(new CallItem("예술종합대학-부학장", "0516634951"));
        callDatas.add(new CallItem("예술종합대학-제3통합행정실팀장", "0516634402"));
        callDatas.add(new CallItem("예술종합대학-제3통합행정실", "0516634902"));
        callDatas.add(new CallItem("예술종합대학-FAX", "0516634909"));
        callDatas.add(new CallItem("예술종합대학-제1전산실", "0516634908"));
        callDatas.add(new CallItem("예술종합대학-제2전산실", "0516635118"));
        callDatas.add(new CallItem("스포츠건강학부-학부장", "0516634951"));
        callDatas.add(new CallItem("스포츠건강학부-학부사무실", "0516634956"));
        callDatas.add(new CallItem("스포츠건강학부-이준희", "0516634950"));
        callDatas.add(new CallItem("스포츠건강학부-박종진", "0516634953"));
        callDatas.add(new CallItem("스포츠건강학부-최승준(체육학)", "0516634955"));
        callDatas.add(new CallItem("스포츠건강학부-전병환(스포츠의학)", "0516634951"));
        callDatas.add(new CallItem("스포츠건강학부-우혜인", "0516634479"));
        callDatas.add(new CallItem("스포츠건강학부-이정아", "0516634479"));
        callDatas.add(new CallItem("스포츠건강학부-조현민", "0516634478"));
        callDatas.add(new CallItem("스포츠건강학부-체육학전공사무실", "0516634956"));
        callDatas.add(new CallItem("스포츠건강학부-스포츠의학전공사무실", "0516634959"));
        callDatas.add(new CallItem("스포츠건강학부-운동과학실험실", "0516634958"));
        callDatas.add(new CallItem("음악학부-학부장", "0516634911"));
        callDatas.add(new CallItem("음악학부-학부사무실", "0516634918"));
        callDatas.add(new CallItem("음악학부-조현선", "0516634915"));
        callDatas.add(new CallItem("음악학부-황정미", "0516634916"));
        callDatas.add(new CallItem("음악학부-임병원", "0516634917"));
        callDatas.add(new CallItem("음악학부-이기균", "0516634911"));
        callDatas.add(new CallItem("음악학부-김성규", "0516634822"));
        callDatas.add(new CallItem("음악학부-최윤희", "0516634821"));
        callDatas.add(new CallItem("음악학부-정경님", "0516634824"));
        callDatas.add(new CallItem("음악학부-김원명", "0516634919"));
        callDatas.add(new CallItem("음악학부-김종욱", "0516634823"));
        callDatas.add(new CallItem("음악학부-김가영", "0516634912"));
        callDatas.add(new CallItem("음악학부-알렉세이", "0516634385"));
        callDatas.add(new CallItem("음악학부-음악도서실", "0516634918"));
        callDatas.add(new CallItem("디자인학부-학부장", "0516635172"));
        callDatas.add(new CallItem("디자인학부-학부사무실", "0516635170"));
        callDatas.add(new CallItem("디자인학부-정한경(시각디자인학)", "0516635164"));
        callDatas.add(new CallItem("디자인학부-고영진", "0516635163"));
        callDatas.add(new CallItem("디자인학부-이방원", "0516634478"));
        callDatas.add(new CallItem("디자인학부-정찬수(제품디자인학)", "0516635172"));
        callDatas.add(new CallItem("디자인학부-이호숭", "0516635174"));
        callDatas.add(new CallItem("디자인학부-김현정", "0516635175"));
        callDatas.add(new CallItem("디자인학부-이해구", "0516635173"));
        callDatas.add(new CallItem("디자인학부-박봉관", "0516634489"));
        callDatas.add(new CallItem("디자인학부-시각디자인학전공사무실", "0516635160"));
        callDatas.add(new CallItem("디자인학부-제품디자인학전공사무실", "0516635170"));
        callDatas.add(new CallItem("연극영화학부-학부장", "0516635185"));
        callDatas.add(new CallItem("연극영화학부-학부사무실", "0516635180"));
        callDatas.add(new CallItem("연극영화학부-허은", "0516635182"));
        callDatas.add(new CallItem("연극영화학부-이성섭(연극)", "0516635185"));
        callDatas.add(new CallItem("연극영화학부-한진수", "0516635186"));
        callDatas.add(new CallItem("연극영화학부-이기호", "0516635187"));
        callDatas.add(new CallItem("연극영화학부-김숙경", "0516635951"));
        callDatas.add(new CallItem("연극영화학부-전수일", "0516635183"));
        callDatas.add(new CallItem("연극영화학부-양영철", "0516635184"));
        callDatas.add(new CallItem("연극영화학부-남명지", "0516634479"));
        callDatas.add(new CallItem("연극영화학부-김진해", "0516635188"));
        callDatas.add(new CallItem("연극영화학부-강내영(영화)", "0516635181"));
        callDatas.add(new CallItem("연극영화학부-조재현", "0516635189"));
        callDatas.add(new CallItem("연극영화학부-김상오", "0516634489"));
        callDatas.add(new CallItem("연극영화학부-안동규", "0516634491"));
        callDatas.add(new CallItem("연극영화학부-김영진", "0516634491"));
        callDatas.add(new CallItem("연극영화학부-조창주", "0516635943"));
        callDatas.add(new CallItem("연극영화학부-유상곤", "0516635943"));
        callDatas.add(new CallItem("연극영화학부-연극전공사무실", "0516635180"));
        callDatas.add(new CallItem("연극영화학부-영화전공사무실", "0516635180"));
        callDatas.add(new CallItem("영상애니메이션학부-학부장", "0516635205"));
        callDatas.add(new CallItem("영상애니메이션학부-학부사무실", "0516635202"));
        callDatas.add(new CallItem("영상애니메이션학부-오종환(영상학)", "0516635106"));
        callDatas.add(new CallItem("영상애니메이션학부-이상조", "0516634491"));
        callDatas.add(new CallItem("영상애니메이션학부-백성웅", "0516635943"));
        callDatas.add(new CallItem("영상애니메이션학부-유왕윤", "0516634489"));
        callDatas.add(new CallItem("영상애니메이션학부-최성규", "0516635103"));
        callDatas.add(new CallItem("영상애니메이션학부-김경애", "0516635104"));
        callDatas.add(new CallItem("영상애니메이션학부-박민주", "0516635105"));
        callDatas.add(new CallItem("영상애니메이션학부-이석호(애니메이션학)", "0516635205"));
        callDatas.add(new CallItem("영상애니메이션학부-영상학전공사무실", "0516635201"));
        callDatas.add(new CallItem("영상애니메이션학부-애니메이션학전공사무실", "0516635202"));
        callDatas.add(new CallItem("디지털미디어학부-학부장", "0516635107"));
        callDatas.add(new CallItem("디지털미디어학부-학부사무실", "0516635203"));
        callDatas.add(new CallItem("디지털미디어학부-FAX", "0516635209"));
        callDatas.add(new CallItem("디지털미디어학부-김재명", "0516635108"));
        callDatas.add(new CallItem("디지털미디어학부-권만우", "0516635102"));
        callDatas.add(new CallItem("디지털미디어학부-김선진", "0516635107"));
        callDatas.add(new CallItem("디지털미디어학부-이상호", "0516635204"));
        callDatas.add(new CallItem("디지털미디어학부-이인혜", "0516634480"));
        callDatas.add(new CallItem("디지털미디어학부-백상훈", "0516634491"));
        callDatas.add(new CallItem("미술학과-박소현", "0516634923"));
        callDatas.add(new CallItem("미술학과-심준섭", "0516634925"));
        callDatas.add(new CallItem("미술학과-학과사무실", "0516634926"));
        callDatas.add(new CallItem("공예디자인학과-권향아", "0516634943"));
        callDatas.add(new CallItem("공예디자인학과-권상인", "0516634941"));
        callDatas.add(new CallItem("공예디자인학과-김진옥", "0516634947"));
        callDatas.add(new CallItem("공예디자인학과-정희균", "0516634944"));
        callDatas.add(new CallItem("공예디자인학과-김지은", "0516634942"));
        callDatas.add(new CallItem("공예디자인학과-김태환", "0516634940"));
        callDatas.add(new CallItem("공예디자인학과-박민찬", "0516634478"));
        callDatas.add(new CallItem("공예디자인학과-박수용", "0516635943"));
        callDatas.add(new CallItem("공예디자인학과-학과사무실", "0516634946"));
        callDatas.add(new CallItem("공예디자인학과-도자기실", "0516634945"));
        callDatas.add(new CallItem("무용학과-최은희", "0516634960"));
        callDatas.add(new CallItem("무용학과-신정희", "0516634963"));
        callDatas.add(new CallItem("무용학과-한혜리", "0516634961"));
        callDatas.add(new CallItem("무용학과-학과사무실", "0516634964"));
        callDatas.add(new CallItem("사진학과-오승환", "0516635192"));
        callDatas.add(new CallItem("사진학과-이재구", "0516635193"));
        callDatas.add(new CallItem("사진학과-황철희", "0516635194"));
        callDatas.add(new CallItem("사진학과-김강", "0516635198"));
        callDatas.add(new CallItem("사진학과-김문정", "0516635198"));
        callDatas.add(new CallItem("사진학과-학과사무실", "0516635190"));
        callDatas.add(new CallItem("패션디자인학과-박옥련", "0516634664"));
        callDatas.add(new CallItem("패션디자인학과-박숙현", "0516634663"));
        callDatas.add(new CallItem("패션디자인학과-김호정", "0516634666"));
        callDatas.add(new CallItem("패션디자인학과-이영주", "0516634667"));
        callDatas.add(new CallItem("패션디자인학과-이경림", "0516634495"));
        callDatas.add(new CallItem("패션디자인학과-장성환", "0516635950"));
        callDatas.add(new CallItem("패션디자인학과-박근수", "0516634479"));
        callDatas.add(new CallItem("패션디자인학과-신장희", "0516634479"));
        callDatas.add(new CallItem("패션디자인학과-학과사무실", "0516634660"));
        callDatas.add(new CallItem("스포츠건강과학연구소-소장", "0516634951"));
        callDatas.add(new CallItem("멀티미디어산학연구소-소장", "0516635205"));
        callDatas.add(new CallItem("신학과-나동광", "0516634814"));
        callDatas.add(new CallItem("신학과-김은정", "0516634813"));
        callDatas.add(new CallItem("신학과-김충만", "0516634113"));
        callDatas.add(new CallItem("신학과-학과사무실", "0516634810"));
        callDatas.add(new CallItem("신학과-FAX", "0516634899"));
        callDatas.add(new CallItem("신학과-전산실", "0516634898"));
        callDatas.add(new CallItem("단과대학통합행정실-팀장", "0516634203"));
        callDatas.add(new CallItem("창의인재대학-학장", "0516634830"));
        callDatas.add(new CallItem("교양교육부-팀장", "0516634020"));
        callDatas.add(new CallItem("교양교육부-행정실", "0516634028"));
        callDatas.add(new CallItem("교양교육부-FAX", "0516634398"));
        callDatas.add(new CallItem("교양교육부-부속실", "0516634832"));
        callDatas.add(new CallItem("교양교육부-강기남", "0516635950"));
        callDatas.add(new CallItem("교양교육부-류은정", "0516634495"));
        callDatas.add(new CallItem("교양교육부-김병환", "0516634498"));
        callDatas.add(new CallItem("교양교육부-김지숙", "0516635955"));
        callDatas.add(new CallItem("교양교육부-선혜진", "0516635955"));
        callDatas.add(new CallItem("교양교육부-이자형", "0516635955"));
        callDatas.add(new CallItem("교양교육부-한미경", "0516635955"));
        callDatas.add(new CallItem("교양교육부-김영희", "0516634477"));
        callDatas.add(new CallItem("교양교육부-김희진", "0516634497"));
        callDatas.add(new CallItem("교양교육부-김미라", "0516634497"));
        callDatas.add(new CallItem("교양교육부-심경옥", "0516635951"));
        callDatas.add(new CallItem("교양교육부-전광수", "0516634486"));
        callDatas.add(new CallItem("교양교육부-이소의", "0516635955"));
        callDatas.add(new CallItem("교양교육부-이재경", "0516635944"));
        callDatas.add(new CallItem("교양교육부-김옥선", "0516635944"));
        callDatas.add(new CallItem("교양교육부-김세경", "0516635944"));
        callDatas.add(new CallItem("교양교육부-정재형", "0516634486"));
        callDatas.add(new CallItem("교양교육부-로버트", "0516634390"));
        callDatas.add(new CallItem("교양교육부-죤,제프,메이", "0516634391"));
        callDatas.add(new CallItem("교양교육부-흄즈,토마스", "0516634392"));
        callDatas.add(new CallItem("교양교육부-인드라", "0516634393"));
        callDatas.add(new CallItem("교양교육부-레스터,크리스", "0516634394"));
        callDatas.add(new CallItem("교양교육부-칼,마이클", "0516634395"));
        callDatas.add(new CallItem("교양교육부-하젤", "0516634396"));
        callDatas.add(new CallItem("교양교육부-실용영어회화담당", "0516634238"));
        callDatas.add(new CallItem("교양교육부-실용영어담당", "0516634239"));
        callDatas.add(new CallItem("인성교육센터-소장", "0516634471"));
        callDatas.add(new CallItem("인문과학연구소-소장", "0516634342"));
        callDatas.add(new CallItem("인문과학연구소-연구실", "0516634277"));
        callDatas.add(new CallItem("한국학연구소-소장", "0516634285"));
        callDatas.add(new CallItem("한국학연구소-연구실", "0516634275"));
        callDatas.add(new CallItem("사회과학연구소-소장", "0516634546"));
        callDatas.add(new CallItem("사회과학연구소-연구실", "0516634506"));
        callDatas.add(new CallItem("산업개발연구소-소장", "0516634411"));
        callDatas.add(new CallItem("산업개발연구소-연구실", "0516634419"));
        callDatas.add(new CallItem("공학기술연구소-소장", "0516634792"));
        callDatas.add(new CallItem("공학기술연구소-연구실", "0516635497"));
        callDatas.add(new CallItem("환경문제연구소-소장", "0516634737"));
        callDatas.add(new CallItem("환경문제연구소-연구실", "0516634739"));
        callDatas.add(new CallItem("한국한자연구소-소장", "0516634266"));
        callDatas.add(new CallItem("한국한자연구소-연구실", "0516634279"));
        callDatas.add(new CallItem("학생회-총학생회장", "0516635700"));
        callDatas.add(new CallItem("학생회-총학생회", "0516635701"));
        callDatas.add(new CallItem("학생회-문과대학생회", "0516635703"));
        callDatas.add(new CallItem("학생회-법정대학생회", "0516635704"));
        callDatas.add(new CallItem("학생회-상경대학생회", "0516635705"));
        callDatas.add(new CallItem("학생회-이과대학생회", "0516635706"));
        callDatas.add(new CallItem("학생회-공과대학생회", "0516635707"));
        callDatas.add(new CallItem("학생회-약학대학생회", "0516635708"));
        callDatas.add(new CallItem("학생회-예술종합대학생회", "0516635709"));
        callDatas.add(new CallItem("학생회-도서관자치위원회", "0516635714"));
        callDatas.add(new CallItem("학생회-동아리연합회", "0516635800"));
        callDatas.add(new CallItem("학생회-대학원학생회", "0516635717"));
        callDatas.add(new CallItem("문과대학회-국어국문학", "0516635720"));
        callDatas.add(new CallItem("문과대학회-일어일문학", "0516635724"));
        callDatas.add(new CallItem("문과대학회-한문학", "0516635726"));
        callDatas.add(new CallItem("문과대학회-사학", "0516635727"));
        callDatas.add(new CallItem("문과대학회-글로컬문화학부", "0516635719"));
        callDatas.add(new CallItem("문과대학회-프랑스지역학", "0516635723"));
        callDatas.add(new CallItem("문과대학회-독일지역학", "0516635722"));
        callDatas.add(new CallItem("문과대학회-철학", "0516635728"));
        callDatas.add(new CallItem("문과대학회-영어영문학", "0516635721"));
        callDatas.add(new CallItem("문과대학회-중국학", "0516635725"));
        callDatas.add(new CallItem("문과대학회-문헌정보학", "0516635729"));
        callDatas.add(new CallItem("문과대학회-교육학", "0516635730"));
        callDatas.add(new CallItem("문과대학회-유아교육", "0516635731"));
        callDatas.add(new CallItem("문과대학회-윤리교육", "0516635732"));
        callDatas.add(new CallItem("법정대학회-법학", "0516635752"));
        callDatas.add(new CallItem("법정대학회-행정학", "0516635750"));
        callDatas.add(new CallItem("법정대학회-정치외교학", "0516635751"));
        callDatas.add(new CallItem("법정대학회-신문방송학", "0516635797"));
        callDatas.add(new CallItem("법정대학회-광고홍보학", "0516635798"));
        callDatas.add(new CallItem("법정대학회-사회복지학", "0516635753"));
        callDatas.add(new CallItem("상경대학회-경제금융학", "0516635740"));
        callDatas.add(new CallItem("상경대학회-물류학", "0516635737"));
        callDatas.add(new CallItem("상경대학회-호텔관광경영학", "0516635739"));
        callDatas.add(new CallItem("상경대학회-외식서비스경영학", "0516635738"));
        callDatas.add(new CallItem("상경대학회-경영학", "0516635742"));
        callDatas.add(new CallItem("상경대학회-국제무역통상학", "0516635741"));
        callDatas.add(new CallItem("상경대학회-회계학", "0516635743"));
        callDatas.add(new CallItem("이과대학회-수학", "0516635760"));
        callDatas.add(new CallItem("이과대학회-응용통계학", "0516635761"));
        callDatas.add(new CallItem("이과대학회-화학", "0516635763"));
        callDatas.add(new CallItem("이과대학회-생명과학", "0516635764"));
        callDatas.add(new CallItem("이과대학회-에너지과학", "0516635762"));
        callDatas.add(new CallItem("이과대학회-물리치료학", "0516635759"));
        callDatas.add(new CallItem("이과대학회-간호학", "0516635758"));
        callDatas.add(new CallItem("이과대학회-식품영양·건강생활학", "0516635765"));
        callDatas.add(new CallItem("공과대학회-환경공학", "0516635773"));
        callDatas.add(new CallItem("공과대학회-토목공학", "0516635774"));
        callDatas.add(new CallItem("공과대학회-도시공학", "0516635776"));
        callDatas.add(new CallItem("공과대학회-전자공학", "0516635783"));
        callDatas.add(new CallItem("공과대학회-전기공학", "0516635777"));
        callDatas.add(new CallItem("공과대학회-메카트로닉스", "0516635779"));
        callDatas.add(new CallItem("공과대학회-산업경영", "0516635771"));
        callDatas.add(new CallItem("공과대학회-식품생명공학", "0516635770"));
        callDatas.add(new CallItem("공과대학회-식품영양학", "0516635765"));
        callDatas.add(new CallItem("공과대학회-건축학", "0516635775"));
        callDatas.add(new CallItem("공과대학회-실내건축디자인학", "0516635768"));
        callDatas.add(new CallItem("공과대학회-컴퓨터공학", "0516635778"));
        callDatas.add(new CallItem("공과대학회-정보통신공학", "0516635786"));
        callDatas.add(new CallItem("공과대학회-신소재공학", "0516635772"));
        callDatas.add(new CallItem("약학대학회-약학", "0516635780"));
        callDatas.add(new CallItem("예술종합대학회-체육학", "0516635793"));
        callDatas.add(new CallItem("예술종합대학회-스포츠의학", "0516635793"));
        callDatas.add(new CallItem("예술종합대학회-음악학", "0516635790"));
        callDatas.add(new CallItem("예술종합대학회-시각디자인학", "0516635767"));
        callDatas.add(new CallItem("예술종합대학회-제품디자인학", "0516635769"));
        callDatas.add(new CallItem("예술종합대학회-연극학", "0516635787"));
        callDatas.add(new CallItem("예술종합대학회-영화학", "0516635789"));
        callDatas.add(new CallItem("예술종합대학회-영상학", "0516635796"));
        callDatas.add(new CallItem("예술종합대학회-애니메이션학", "0516635795"));
        callDatas.add(new CallItem("예술종합대학회-디지털미디어", "0516635799"));
        callDatas.add(new CallItem("예술종합대학회-미술학", "0516635791"));
        callDatas.add(new CallItem("예술종합대학회-공예디자인학", "0516635792"));
        callDatas.add(new CallItem("예술종합대학회-무용학", "0516635794"));
        callDatas.add(new CallItem("예술종합대학회-사진학", "0516635788"));
        callDatas.add(new CallItem("예술종합대학회-패션디자인학", "0516635766"));
        callDatas.add(new CallItem("예술종합대학회-신학", "0516635781"));

        return callDatas;
    }
}
