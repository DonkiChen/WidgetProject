package test.widgetproject.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.widgetproject.adapter.ShareAdapter;
import test.widgetproject.util.TransitionHelper;

public class ShareFragment extends Fragment implements TransitionHelper.ReenterListener {

    @BindView(R.id.rv_images)
    RecyclerView mRvImages;

    private ShareAdapter mShareAdapter;
    private TransitionHelper mTransitionHelper;

    public static ShareFragment newInstance() {

        Bundle args = new Bundle();

        ShareFragment fragment = new ShareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_share, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvImages.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mShareAdapter = new ShareAdapter(getActivity());
        mRvImages.setAdapter(mShareAdapter);
        List<String> urls = new ArrayList<>();
        urls.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=314412909,2562380588&fm=27&gp=0.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=d7da51e77b5b90451cffd8ba2fcfe7f1&imgtype=0&src=http%3A%2F%2Fimg0.ph.126.net%2FWdsXG_x162KnMYfBWgCftg%3D%3D%2F6608597046561718434.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=f98ff2f7e3eb3120eafbb8280d8b22e5&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727125453_AP4Bi.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=22a4e5e1490b1063745572cbb70ef7eb&imgtype=0&src=http%3A%2F%2Fuploads.xuexila.com%2Fallimg%2F1707%2F163G1K38-5.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=bfce3482a266c3c4c33d748aba27027d&imgtype=0&src=http%3A%2F%2Fi9.download.fd.pchome.net%2Ft_600x1024%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-Y-IJnBVABWjDqspLdkAABz9QK8BzIAFaMm849.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=c99251a572ea8999588421cf3bea983b&imgtype=0&src=http%3A%2F%2Fi6.download.fd.pchome.net%2Ft_640x960%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-UyIKhsjABb_zO2dLW4AABz9QIpOKsAFv_k111.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=7a22e6b11310b0f51c1cff92be14f5a0&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171109%2Fc17061b7d62a40b28735d25d1b9b3e1b.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=37a2bae5611e09371a6d9cc853d83114&imgtype=0&src=http%3A%2F%2Fk.zol-img.com.cn%2Fwallpaper%2F7766%2F7765481_0540.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=b3438f4794e1841671f0627055964101&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727020945_vX54S.jpeg");
        urls.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=314412909,2562380588&fm=27&gp=0.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=d7da51e77b5b90451cffd8ba2fcfe7f1&imgtype=0&src=http%3A%2F%2Fimg0.ph.126.net%2FWdsXG_x162KnMYfBWgCftg%3D%3D%2F6608597046561718434.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=f98ff2f7e3eb3120eafbb8280d8b22e5&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727125453_AP4Bi.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=22a4e5e1490b1063745572cbb70ef7eb&imgtype=0&src=http%3A%2F%2Fuploads.xuexila.com%2Fallimg%2F1707%2F163G1K38-5.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=bfce3482a266c3c4c33d748aba27027d&imgtype=0&src=http%3A%2F%2Fi9.download.fd.pchome.net%2Ft_600x1024%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-Y-IJnBVABWjDqspLdkAABz9QK8BzIAFaMm849.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=c99251a572ea8999588421cf3bea983b&imgtype=0&src=http%3A%2F%2Fi6.download.fd.pchome.net%2Ft_640x960%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-UyIKhsjABb_zO2dLW4AABz9QIpOKsAFv_k111.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=7a22e6b11310b0f51c1cff92be14f5a0&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171109%2Fc17061b7d62a40b28735d25d1b9b3e1b.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=37a2bae5611e09371a6d9cc853d83114&imgtype=0&src=http%3A%2F%2Fk.zol-img.com.cn%2Fwallpaper%2F7766%2F7765481_0540.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=b3438f4794e1841671f0627055964101&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727020945_vX54S.jpeg");
        urls.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=314412909,2562380588&fm=27&gp=0.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=d7da51e77b5b90451cffd8ba2fcfe7f1&imgtype=0&src=http%3A%2F%2Fimg0.ph.126.net%2FWdsXG_x162KnMYfBWgCftg%3D%3D%2F6608597046561718434.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=f98ff2f7e3eb3120eafbb8280d8b22e5&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727125453_AP4Bi.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=22a4e5e1490b1063745572cbb70ef7eb&imgtype=0&src=http%3A%2F%2Fuploads.xuexila.com%2Fallimg%2F1707%2F163G1K38-5.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=bfce3482a266c3c4c33d748aba27027d&imgtype=0&src=http%3A%2F%2Fi9.download.fd.pchome.net%2Ft_600x1024%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-Y-IJnBVABWjDqspLdkAABz9QK8BzIAFaMm849.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=c99251a572ea8999588421cf3bea983b&imgtype=0&src=http%3A%2F%2Fi6.download.fd.pchome.net%2Ft_640x960%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-UyIKhsjABb_zO2dLW4AABz9QIpOKsAFv_k111.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=7a22e6b11310b0f51c1cff92be14f5a0&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171109%2Fc17061b7d62a40b28735d25d1b9b3e1b.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=37a2bae5611e09371a6d9cc853d83114&imgtype=0&src=http%3A%2F%2Fk.zol-img.com.cn%2Fwallpaper%2F7766%2F7765481_0540.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=b3438f4794e1841671f0627055964101&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727020945_vX54S.jpeg");
        urls.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=314412909,2562380588&fm=27&gp=0.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=d7da51e77b5b90451cffd8ba2fcfe7f1&imgtype=0&src=http%3A%2F%2Fimg0.ph.126.net%2FWdsXG_x162KnMYfBWgCftg%3D%3D%2F6608597046561718434.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=f98ff2f7e3eb3120eafbb8280d8b22e5&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727125453_AP4Bi.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121068&di=22a4e5e1490b1063745572cbb70ef7eb&imgtype=0&src=http%3A%2F%2Fuploads.xuexila.com%2Fallimg%2F1707%2F163G1K38-5.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=bfce3482a266c3c4c33d748aba27027d&imgtype=0&src=http%3A%2F%2Fi9.download.fd.pchome.net%2Ft_600x1024%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-Y-IJnBVABWjDqspLdkAABz9QK8BzIAFaMm849.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=c99251a572ea8999588421cf3bea983b&imgtype=0&src=http%3A%2F%2Fi6.download.fd.pchome.net%2Ft_640x960%2Fg1%2FM00%2F0A%2F0C%2FoYYBAFPe-UyIKhsjABb_zO2dLW4AABz9QIpOKsAFv_k111.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=7a22e6b11310b0f51c1cff92be14f5a0&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171109%2Fc17061b7d62a40b28735d25d1b9b3e1b.jpeg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=37a2bae5611e09371a6d9cc853d83114&imgtype=0&src=http%3A%2F%2Fk.zol-img.com.cn%2Fwallpaper%2F7766%2F7765481_0540.jpg");
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536900121067&di=b3438f4794e1841671f0627055964101&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727020945_vX54S.jpeg");
        mShareAdapter.setNewData(urls);

        mTransitionHelper = new TransitionHelper(getActivity()) {
            @Override
            public RecyclerView getRecyclerView() {
                return mRvImages;
            }

            @Override
            public Map<String, View> getSharedElements(int position) {
                final BaseViewHolder baseViewHolder = (BaseViewHolder) getRecyclerView().findViewHolderForLayoutPosition(position);
                if (baseViewHolder != null) {
                    View view = baseViewHolder.getView(R.id.image);
                    Map<String, View> map = new HashMap<>();
                    map.put(view.getTransitionName(), view);
                    return map;
                }
                return null;
            }
        };
    }

    @Override
    public void onReenter(int resultCode, Intent data) {
        mTransitionHelper.onReenter(data);
    }
}
