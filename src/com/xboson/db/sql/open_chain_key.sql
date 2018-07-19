SELECT
      `chain_name`,
      `channel_name`,
      `type`,
      `publickey`,
      `privatekey`,
      `chain_id`,

FROM  `sys_chain_signer`
Where status = '1'
  And chain_name = ?
  And channel_name = ?