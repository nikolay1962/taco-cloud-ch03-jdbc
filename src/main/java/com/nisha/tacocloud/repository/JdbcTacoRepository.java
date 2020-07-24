package com.nisha.tacocloud.repository;

import com.nisha.tacocloud.domain.Ingredient;
import com.nisha.tacocloud.domain.Taco;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class JdbcTacoRepository implements TacoRepository{

    private final JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert tacoInserter;

    public JdbcTacoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.tacoInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("Taco")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Taco save(Taco taco) {
        long tacoId = saveTacoInfo(taco);
        taco.setId(tacoId);
        for (Ingredient ingredient : taco.getIngredients()) {
            saveIngredientToTaco(ingredient, tacoId);
        }
        return taco;
    }

    private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
        jdbcTemplate.update(
                "insert into Taco_Ingredients (taco, ingredient) values (?, ?)",
                tacoId, ingredient.getId()
        );
    }

    private long saveTacoInfo(Taco taco) {
        taco.setCreatedAt(new Date());
        /*PreparedStatementCreator psc =
                new PreparedStatementCreatorFactory(
                        "insert into Taco (name, createdAt) values (?, ?)",
                        Types.VARCHAR, Types.TIMESTAMP
                ).newPreparedStatementCreator(
                        Arrays.asList(
                                taco.getName(),
                                new Timestamp(taco.getCreatedAt().getTime())));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = jdbcTemplate.update(psc, keyHolder);
        log.warn("update result:" + update);
        log.warn("keyHolder:" + keyHolder.getKeyList());
        return keyHolder.getKey().longValue();*/

        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("name", taco.getName());
        parameters.put("createdAt", new Timestamp(taco.getCreatedAt().getTime()));
        Number newId = tacoInserter.executeAndReturnKey(parameters);
        log.warn("Number newId:" + newId);
        return newId.longValue();
    }

}
