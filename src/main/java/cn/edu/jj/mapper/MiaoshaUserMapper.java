package cn.edu.jj.mapper;

import cn.edu.jj.domain.MiaoshaUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MiaoshaUserMapper {
    @Select("SELECT * FROM miaosha_user")
    public List<MiaoshaUser> findAll();

    @Select("SELECT * FROM miaosha_user WHERE id = #{id}")
    public MiaoshaUser findById(@Param("id") long id);

    @Insert("INSERT INTO miaosha_user(id,nickname,password,salt) VALUES(#{id},#{nickname},#{password},#{salt})")
    public void insertUser(MiaoshaUser user);

    @Update("UPDATE miaosha_user SET password=#{password} WHERE id=#{id}")
    void updatePassword(MiaoshaUser toBeUpdate);
}
